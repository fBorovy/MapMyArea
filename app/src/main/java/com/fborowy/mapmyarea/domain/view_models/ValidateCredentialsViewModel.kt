package com.fborowy.mapmyarea.domain.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ValidateCredentialsViewModel: ViewModel() {

    var password1 by mutableStateOf("")
        private set
    var password2 by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
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

    fun updateEmailField(newEmail: String) {
        email = newEmail
    }

    fun updatePassword1Field(newPass1: String) {
        password1 = newPass1
    }

    fun updatePassword2Field(newPass2: String) {
        password2 = newPass2
    }
}