package com.fborowy.mapmyarea.domain.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fborowy.mapmyarea.data.EmailAuthClient
import com.fborowy.mapmyarea.domain.states.SignInResult
import kotlinx.coroutines.launch

class RegistrationViewModel: ViewModel() {

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

    fun signUp(emailAuthClient: EmailAuthClient, onSignUpClick: (SignInResult) -> Unit){
        viewModelScope.launch {
            try {
                emailAuthClient.signUpWithEmail(
                    email,
                    password2,
                ) {
                    onSignUpClick(it)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}