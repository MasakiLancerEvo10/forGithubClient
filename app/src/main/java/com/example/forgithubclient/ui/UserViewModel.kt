package com.example.forgithubclient.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forgithubclient.data.GitHubService
import com.example.forgithubclient.data.User
import com.example.forgithubclient.data.UserDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

enum class SortOrder {
    ID, NAME, REPOS
}

class UserViewModel : ViewModel() {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "forGithubClient-App")
                .header("Accept", "application/vnd.github.v3+json")
                // .header("Authorization", "token YOUR_TOKEN") // レート制限を緩和したい場合はここに追加
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(GitHubService::class.java)

    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    private val _sortOrder = MutableStateFlow(SortOrder.ID)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val sortedUsersState: StateFlow<UiState<List<User>>> = combine(_usersState, _sortOrder) { state, order ->
        if (state is UiState.Success) {
            val sortedList = when (order) {
                SortOrder.ID -> state.data.sortedBy { it.id }
                SortOrder.NAME -> state.data.sortedBy { it.login.lowercase() }
                SortOrder.REPOS -> state.data.sortedByDescending { it.publicRepos ?: 0 }
            }
            UiState.Success(sortedList)
        } else {
            state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Idle)

    private val _userDetailState = MutableStateFlow<UiState<UserDetail>>(UiState.Idle)
    val userDetailState: StateFlow<UiState<UserDetail>> = _userDetailState.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            try {
                val basicUsers = service.getUsers()
                
                val enrichedUsers = basicUsers.map { user ->
                    async {
                        try {
                            val detail = service.getUserDetail(user.login)
                            user.copy(
                                name = detail.name, 
                                publicRepos = detail.publicRepos,
                                company = detail.company
                            )
                        } catch (e: Exception) {
                            user
                        }
                    }
                }.awaitAll()

                _usersState.value = UiState.Success(enrichedUsers)
            } catch (e: Exception) {
                handleError(e) { _usersState.value = it }
            }
        }
    }

    private fun handleError(e: Exception, updateState: (UiState.Error) -> Unit) {
        val message = when (e) {
            is HttpException -> {
                if (e.code() == 403) {
                    "APIレート制限に達しました。しばらく時間を置いてから再度お試しください。"
                } else {
                    "サーバーエラーが発生しました (${e.code()})"
                }
            }
            else -> e.localizedMessage ?: "接続エラーが発生しました"
        }
        updateState(UiState.Error(message))
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun fetchUserDetail(username: String) {
        viewModelScope.launch {
            _userDetailState.value = UiState.Loading
            try {
                val response = service.getUserDetail(username)
                _userDetailState.value = UiState.Success(response)
            } catch (e: Exception) {
                handleError(e) { _userDetailState.value = it }
            }
        }
    }
    
    fun clearUserDetail() {
        _userDetailState.value = UiState.Idle
    }
}
