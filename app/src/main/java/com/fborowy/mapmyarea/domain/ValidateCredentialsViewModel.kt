package com.fborowy.mapmyarea.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ValidateCredentialsViewModel: ViewModel() {

    var password1 by mutableStateOf("")
    var password2 by mutableStateOf("")
    var email by mutableStateOf("")
    private val minimalPasswordLength = 6

    fun validate(): Int {
        if (password1.length < minimalPasswordLength)
            return 1
        if (password1 != password2)
            return 2
        if (email == "")
            return 3
        return 0
    }
}