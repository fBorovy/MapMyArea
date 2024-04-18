package com.fborowy.mapmyarea.domain.email_auth

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.domain.SignInResult
import com.fborowy.mapmyarea.data.UserData
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CancellationException

class EmailAuthClient(
    private val auth: FirebaseAuth,
): ViewModel() {
    fun signUpWithEmail(email: String, password: String, onComplete: (SignInResult) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val result = if (task.isSuccessful) {
                    val user = auth.currentUser
                    SignInResult(
                        data = user?.run {
                            UserData(
                                userId = uid,
                                username = displayName,
                            )
                        },
                        errorMessage = null
                    )
                } else {
                    val e = task.exception
                    e?.printStackTrace()
                    if (e is CancellationException) throw e
                    SignInResult(
                        data = null,
                        errorMessage = e.toString()
                    )
                }
                onComplete(result)
            }
    }

    fun signInWithEmail(email: String, password: String, onComplete: (SignInResult) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val result = if (task.isSuccessful) {
                    val user = auth.currentUser
                    SignInResult(
                        data = user?.run {
                            UserData(
                                userId = uid,
                                username = displayName,
                            )
                        },
                        errorMessage = null
                    )
                } else {
                    val e = task.exception
                    e?.printStackTrace()
                    if (e is CancellationException) throw e
                    SignInResult(
                        data = null,
                        errorMessage = e.toString()
                    )
                }
                onComplete(result)
            }
    }
}