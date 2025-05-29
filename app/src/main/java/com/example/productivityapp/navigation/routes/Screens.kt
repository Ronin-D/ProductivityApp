

sealed class Screens(
    val route: String
) {
    object SignUp : Screens("sign_up")

    object SignIn : Screens("sign_in")

    object AppStatistics: Screens("app_statistics")

    object Profile: Screens("profile")

    object ChatList: Screens("user_chat_list")

    object Chat: Screens("chat")
}