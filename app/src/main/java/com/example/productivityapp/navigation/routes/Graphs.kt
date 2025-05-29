sealed class Graphs(val route: String) {
    object Patient : Graphs(route = "patient_nav")

    object Auth : Graphs(route = "auth_nav")

    object Doctor: Graphs("doctor_nav")

    object Chat: Graphs("chat_nav")
}