package com.example.mobileclient.navigation.routes

sealed class Graphs(val route: String) {
    object Texts : Graphs(route = "texts_nav")

    object Auth : Graphs(route = "auth_nav")
}