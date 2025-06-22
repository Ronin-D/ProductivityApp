package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.app_blocker.AppBlockerRoute
import com.example.app_statistics.AppStatisticsRoute
import com.example.blocked_app_list.BlockedAppListRoute
import com.example.chat_list.ChatListRoute
import com.example.profile.UserProfileRoute

fun NavGraphBuilder.patientNavGraph(navController: NavController) {
    navigation(
        startDestination = Screens.BlockedAppList.route,
        route = Graphs.Patient.route
    ) {
        composable(route = Screens.AppStatistics.route) {
            AppStatisticsRoute()
        }

        composable(route = Screens.UserProfile.route) {
            UserProfileRoute(onSignOut = {
                navController.navigate(Graphs.Auth.route) {
                    popUpTo(Graphs.Patient.route) { inclusive = true }
                }
            })
        }

        composable(route = Screens.ChatList.route) {
            ChatListRoute(
                onChatSelected = { chatId ->
                    navController.navigate("${Screens.Chat.route}/$chatId")
                }
            )
        }

        composable(route = Screens.AppBlocker.route) {
            AppBlockerRoute(onNavigateBack = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("shouldRefresh", true)
                navController.popBackStack()
            })
        }

        composable(route = Screens.BlockedAppList.route) {
            BlockedAppListRoute {
                navController.navigate(Screens.AppBlocker.route)
            }
        }
    }
}
