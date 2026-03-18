package com.example.forgithubclient.data

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{username}")
    suspend fun getUserDetail(@Path("username") username: String): UserDetail
}
