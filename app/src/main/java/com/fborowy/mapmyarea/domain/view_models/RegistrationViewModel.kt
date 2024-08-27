package com.fborowy.mapmyarea.domain.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fborowy.mapmyarea.domain.email_auth.EmailAuthClient
import com.fborowy.mapmyarea.domain.states.SignInResult
import kotlinx.coroutines.launch

class RegistrationViewModel: ViewModel() {

    val maxUsernameLength = 50
    val maxPasswordLength = 30
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
        email = if (newEmail.length <= maxUsernameLength) {
            newEmail
        } else {
            newEmail.take(maxUsernameLength)
        }
    }

    fun updatePassword1Field(newPass1: String) {
        password1 = if (newPass1.length <= maxPasswordLength) {
            newPass1
        } else {
            newPass1.take(maxPasswordLength)
        }
    }

    fun updatePassword2Field(newPass2: String) {
        password2 = if (newPass2.length <= maxPasswordLength) {
            newPass2
        } else {
            newPass2.take(maxPasswordLength)
        }
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