package com.example.productivityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.auth.AppEvent
import com.example.auth.AuthEventBus
import com.example.mobileclient.bottomnav.BottomNavMenu
import com.example.mobileclient.navigation.RootNavGraph
import com.example.mobileclient.navigation.routes.Screens
import com.example.productivityapp.ui.theme.ProductivityAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            LaunchedEffect(Unit) {
                AuthEventBus.events.collect { event ->
                    when (event) {
                        is AppEvent.NavigateToLogin -> {
                            navController.navigate(Screens.SignIn.route) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
            ProductivityAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavMenu(
                            navController = navController,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        RootNavGraph(navController)
                    }
                }
            }
        }
    }
}