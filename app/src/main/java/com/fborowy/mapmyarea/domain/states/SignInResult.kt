package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)