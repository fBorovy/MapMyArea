package com.fborowy.mapmyarea.domain.states

data class SignInState(
    val signInSuccessful: Boolean = false,
    val signInError: String? = null
)
