package com.fborowy.mapmyarea.domain.states

data class SignInState(
    val signInSuccessful: Boolean? = null,
    val signInError: String? = null
)
