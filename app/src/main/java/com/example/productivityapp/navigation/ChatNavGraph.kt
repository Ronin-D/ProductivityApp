package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.chat.ChatRoute

fun NavGraphBuilder.chatNavGraph(navController: NavController) {
    navigation(
        startDestination = Screens.ChatList.route,
        route = Graphs.Chat.route
    ) {
        composable(
            route = "${Screens.Chat.route}/{chatId}",
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            ChatRoute(onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}

