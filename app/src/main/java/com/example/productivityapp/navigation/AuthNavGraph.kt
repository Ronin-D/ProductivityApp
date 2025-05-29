package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.productivityapp.models.Role
import com.example.sign_in.SignInRoute
import com.example.sign_up.SignUpRoute

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation(
        startDestination = Screens.SignIn.route,
        route = Graphs.Auth.route
    ) {
        composable(route = Screens.SignIn.route) {
            SignInRoute(
                onNavigateToMain = { role ->
                    when (Role.byCode(role)) {
                        Role.DOCTOR -> {
                            navController.navigate(Graphs.Doctor.route)
                        }

                        Role.PATIENT -> {
                            navController.navigate(Graphs.Patient.route)
                        }

                        null -> {}
                    }
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