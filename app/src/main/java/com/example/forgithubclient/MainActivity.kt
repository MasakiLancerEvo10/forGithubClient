package com.example.forgithubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forgithubclient.ui.UserDetailScreen
import com.example.forgithubclient.ui.UserListScreen
import com.example.forgithubclient.ui.UserViewModel
import com.example.forgithubclient.ui.theme.ForGithubClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForGithubClientTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // By providing the ViewModel here, it lives as long as the Navigation Graph
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "userList") {
        composable("userList") {
            UserListScreen(
                viewModel = userViewModel,
                onUserClick = { username ->
                    navController.navigate("userDetail/$username")
                }
            )
        }
        composable("userDetail/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserDetailScreen(
                username = username,
                viewModel = userViewModel,
                onBack = {
                    userViewModel.clearUserDetail()
                    navController.popBackStack()
                }
            )
        }
    }
}
