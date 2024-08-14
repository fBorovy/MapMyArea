package com.fborowy.mapmyarea.domain

sealed class Screen(val route: String = "") {
    data object StartScreen: Screen(route = "start_screen")
    data object HomeScreen: Screen(route = "home_screen")
    data object EmailSignUpScreen: Screen(route = "email_sign_up_screen")
    data object EmailSignInScreen: Screen(route = "email_sign_in_screen")
    data object MapCreatorScreen1: Screen(route = "map_creator_screen1")
    data object MapCreatorScreen2: Screen(route = "map_creator_screen2")
    data object MapCreatorScreen3: Screen(route = "map_creator_screen3")
    data object MarkerConfigurationScreen: Screen(route = "marker_configuration_screen")
    data object FloorConfigurationScreen: Screen(route = "floor_configuration_screen")
    data object SavedMapScreen: Screen(route = "saved_map_screen")
    data object CreditsScreen: Screen(route = "credits_screen")

}

