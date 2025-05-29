package com.example.productivityapp.navigation

import Graphs
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
        patientNavGraph(navController)
        doctorNavGraph(navController)
        chatNavGraph(navController)
    }
}