package com.fborowy.mapmyarea.domain

import com.fborowy.mapmyarea.data.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)
