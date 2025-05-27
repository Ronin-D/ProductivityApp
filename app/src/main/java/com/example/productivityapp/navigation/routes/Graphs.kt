sealed class Graphs(val route: String) {
    object User : Graphs(route = "user_role_nav")

    object Auth : Graphs(route = "auth_nav")
}