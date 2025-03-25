package com.example.dicefight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ✅ Persist win count across multiple games
    val playerWinsState = rememberSaveable { mutableStateOf(0) }
    val computerWinsState = rememberSaveable { mutableStateOf(0) }

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable(route = "mainScreen") {
            MainScreen(navController)
        }
        composable(route = "gameScreen") {
            // ✅ Pass the win count states to GameScreen
            GameScreen(navController, playerWinsState, computerWinsState)
        }
    }
}
