package com.fborowy.mapmyarea.domain

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.UserData
import com.fborowy.mapmyarea.domain.google_auth.GoogleAuthClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class AppViewModel: ViewModel() {

    val auth = Firebase.auth
    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()

    fun onSignIn(result: SignInResult) {
        _signInState.update { it.copy(
            signInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }
    fun resetSignInState() {
        _signInState.update { SignInState() }
    }

    fun getSignedUserInfo(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
        )
    }

}