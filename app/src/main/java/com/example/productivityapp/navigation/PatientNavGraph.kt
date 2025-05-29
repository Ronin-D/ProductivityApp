package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.app_statistics.AppStatisticsRoute
import com.example.chat_list.ChatListRoute
import com.example.profile.UserProfileRoute

fun NavGraphBuilder.patientNavGraph(navController: NavController) {
    navigation(
        startDestination = Screens.AppStatistics.route,
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

        composable(route = Screens.DoctorProfile.route) {
            Text("doctor profile")
        }

        composable(route = Screens.PatientList.route) {
            Text("patients")
        }

        composable(
            route = "${Screens.PatientStatistics.route}/{patientId}",
            arguments = listOf(
                navArgument("patientId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            Text("patient statistics")
        }
    }
}
