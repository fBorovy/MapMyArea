package com.fborowy.mapmyarea.presentation.google_sign_in

data class SignInState(
    val signInSuccessful: Boolean = false,
    val signInError: String? = null
)
