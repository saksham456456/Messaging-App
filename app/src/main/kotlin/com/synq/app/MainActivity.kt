package com.synq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.synq.app.core.network.TokenManager
import com.synq.app.core.theme.SynqTheme
import com.synq.app.presentation.navigation.Screen
import com.synq.app.presentation.navigation.SynqNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SynqTheme {
                val navController = rememberNavController()
                val startDestination = if (tokenManager.getToken() != null) {
                    if (tokenManager.isProfileComplete()) Screen.ChatList.route else Screen.ProfileSetup.route
                } else {
                    Screen.Auth.route
                }
                SynqNavGraph(navController = navController, startDestination = startDestination)
            }
        }
    }
}
