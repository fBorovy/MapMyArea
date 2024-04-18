package com.fborowy.mapmyarea.domain

sealed class Screen(val route: String = "") {
    data object StartScreen: Screen(route = "start_screen")
    data object HomeScreen: Screen(route = "home_screen")
    data object EmailSignUpScreen: Screen(route = "email_sign_up_screen")
    data object EmailSignInScreen: Screen(route = "email_sign_in_screen")


}
