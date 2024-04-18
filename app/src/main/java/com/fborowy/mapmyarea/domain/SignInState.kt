package com.fborowy.mapmyarea.domain

data class SignInState(
    val signInSuccessful: Boolean = false,
    val signInError: String? = null
)
