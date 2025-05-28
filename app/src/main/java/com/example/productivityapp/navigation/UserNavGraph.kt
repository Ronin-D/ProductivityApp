package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.app_statistics.AppStatisticsRoute
import com.example.profile.ProfileRoute

fun NavGraphBuilder.userNavGraph(navController: NavController) {
    navigation(
        startDestination = Screens.AppStatistics.route,
        route = Graphs.User.route
    ) {
        composable(route = Screens.AppStatistics.route) {
            AppStatisticsRoute()
        }

        composable(route = Screens.Profile.route) {
            ProfileRoute(onSignOut = {
                navController.navigate(Graphs.Auth.route)
            })
        }
    }
}