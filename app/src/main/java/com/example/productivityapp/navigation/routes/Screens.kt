

sealed class Screens(
    val route: String
) {
    object SignUp : Screens("sign_up")

    object SignIn : Screens("sign_in")

    object AppStatistics: Screens("app_statistics")

    object UserProfile: Screens("user_profile")

    object ChatList: Screens("user_chat_list")

    object Chat: Screens("chat")

    object DoctorProfile: Screens("doctor_profile")

    object PatientList: Screens("patient_list")

    object PatientStatistics: Screens("patient_statistics")
}