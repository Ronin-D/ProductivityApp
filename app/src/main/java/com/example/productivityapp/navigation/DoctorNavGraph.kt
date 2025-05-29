package com.example.productivityapp.navigation

import Graphs
import Screens
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.patient_list.PatientListRoute
import com.example.patient_statistics.PatientStatisticsRoute
import com.example.profile.DoctorProfileRoute

fun NavGraphBuilder.doctorNavGraph(navController: NavController) {

    navigation(
        startDestination = Screens.PatientList.route,
        route = Graphs.Doctor.route
    ) {
        composable(route = Screens.DoctorProfile.route) {
            DoctorProfileRoute {
                navController.navigate(Graphs.Auth.route) {
                    popUpTo(Graphs.Patient.route) { inclusive = true }
                }
            }
        }

        composable(route = Screens.PatientList.route) {
            PatientListRoute(
                onChatSelected = { chatId ->
                    navController.navigate("${Screens.Chat.route}/$chatId")
                },
                onPatientStatisticsSelected = { patientId ->
                    navController.navigate("${Screens.PatientStatistics.route}/$patientId")
                }
            )
        }

        composable(
            route = "${Screens.PatientStatistics.route}/{patientId}",
            arguments = listOf(
                navArgument("patientId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            PatientStatisticsRoute(onNavigateBack = {
                navController.popBackStack()
            })
        }
    }

}
