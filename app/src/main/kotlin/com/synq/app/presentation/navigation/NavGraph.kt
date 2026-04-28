package com.synq.app.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.synq.app.presentation.screens.auth.AuthScreen
import com.synq.app.presentation.screens.chatlist.ChatListScreen
import com.synq.app.presentation.screens.chatdetail.ChatDetailScreen
@Composable fun SynqNavGraph(navController: NavHostController, startDestination: String = Screen.Auth.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.Auth.route) { AuthScreen(onAuthSuccess = { navController.navigate(Screen.ChatList.route) { popUpTo(Screen.Auth.route) { inclusive = true } } }) }
        composable(route = Screen.ChatList.route) { ChatListScreen(onChatClick = { chatId -> navController.navigate(Screen.ChatDetail.createRoute(chatId)) }) }
        composable(route = Screen.ChatDetail.route) { backStackEntry -> val chatId = backStackEntry.arguments?.getString("chatId") ?: ""; ChatDetailScreen(chatId = chatId, onBack = { navController.popBackStack() }) }
    }
}
