package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.app_statistics.AppStatisticsRoute
import com.example.sign_in.SignInRoute
import com.example.sign_up.SignUpRoute

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation(
        startDestination = Screens.SignIn.route,
        route = Graphs.Auth.route
    ) {
        composable(route = Screens.SignIn.route) {
            SignInRoute(
                onNavigateToTexts = {
                    navController.navigate(Graphs.User.route)
                },
                onGoToRegister = {
                    navController.navigate(Screens.SignUp.route)
                }
            )
        }
        composable(route = Screens.SignUp.route) {
            SignUpRoute(
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}