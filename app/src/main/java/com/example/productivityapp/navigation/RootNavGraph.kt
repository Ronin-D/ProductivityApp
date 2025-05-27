package com.example.productivityapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost


@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Graphs.Auth.route
    ) {
        authNavGraph(navController)
        userNavGraph(navController)
    }
}