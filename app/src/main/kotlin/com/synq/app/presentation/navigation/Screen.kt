package com.synq.app.presentation.navigation
sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object ChatList : Screen("chat_list")
    object ChatDetail : Screen("chat_detail/{chatId}") { fun createRoute(chatId: String) = "chat_detail/$chatId" }
}
