package com.synq.app.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.synq.app.presentation.screens.splash.SynqMotionSplash
import com.synq.app.presentation.screens.auth.AuthScreen
import com.synq.app.presentation.screens.profile.ProfileSetupScreen
import com.synq.app.presentation.screens.chatlist.ChatListScreen
import com.synq.app.presentation.screens.newchat.NewChatScreen
import com.synq.app.presentation.screens.chatdetail.ChatDetailScreen

@Composable fun SynqNavGraph(
    navController: NavHostController,
    startDestination: String,
    isInitialLaunch: Boolean = true,
    isAppReady: () -> Boolean
) {
    val actualStart = if (isInitialLaunch) Screen.Splash.route else startDestination

    NavHost(navController = navController, startDestination = actualStart) {
        composable(route = Screen.Splash.route) {
            SynqMotionSplash(
                isAppReady = isAppReady,
                onSplashFinished = {
                    navController.navigate(startDestination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Auth.route) {
            AuthScreen(onAuthSuccess = {
                navController.navigate(Screen.ProfileSetup.route) { popUpTo(Screen.Auth.route) { inclusive = true } }
            })
        }
        composable(route = Screen.ProfileSetup.route) {
            ProfileSetupScreen(onProfileComplete = {
                navController.navigate(Screen.ChatList.route) { popUpTo(Screen.ProfileSetup.route) { inclusive = true } }
            })
        }
        composable(route = Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId -> navController.navigate(Screen.ChatDetail.createRoute(chatId)) },
                onNewChatClick = { navController.navigate(Screen.NewChat.route) }
            )
        }
        composable(route = Screen.NewChat.route) {
            NewChatScreen(
                onBack = { navController.popBackStack() },
                onChatCreated = { newChatId ->
                    navController.navigate(Screen.ChatDetail.createRoute(newChatId)) {
                        popUpTo(Screen.NewChat.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.ChatDetail.route) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(chatId = chatId, onBack = { navController.popBackStack() })
        }
    }
}
