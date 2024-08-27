package com.fborowy.mapmyarea.domain.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.EmailAuthClient
import com.fborowy.mapmyarea.domain.states.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel: ViewModel() {

    var password1 by mutableStateOf("")
        private set
    var password2 by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    private val minimalPasswordLength = 6

    private val _errorCode = MutableStateFlow(0)
    val errorCode: StateFlow<Int> = _errorCode

    fun validate() {
        if (password1.length < minimalPasswordLength)
            _errorCode.update { R.string.short_password_error }
        if (password1 != password2)
            R.string.passwords_differ_error
        if (email == "")
            R.string.empty_email_error
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
                    if (it.errorMessage == "exists") _errorCode.update { R.string.user_already_exists }
                    else onSignUpClick(it)
                }
            } catch (e: Exception) {
                _errorCode.value = R.string.unknown_registering_user_error
            }
        }
    }

    fun resetErrorCode() {
        _errorCode.update { 0 }
    }
}