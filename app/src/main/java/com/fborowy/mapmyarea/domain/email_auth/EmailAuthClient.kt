package com.fborowy.mapmyarea.domain.email_auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.UserData
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.trimEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CancellationException

class EmailAuthClient(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
): ViewModel() {
    fun signUpWithEmail(email: String, password: String, onComplete: (SignInResult) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val result = if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newFirestoreUser = hashMapOf(
                        "username" to trimEmail(user?.email!!),
                        "savedMaps" to null,
                    )
                    Log.d("FIRE", user.uid)
                    database.collection("users")
                        .document(user.uid)
                        .set(newFirestoreUser)
                        .addOnSuccessListener { Log.d("AAAA", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("AAAA", "Error writing document", e) }

                    SignInResult(
                        data = user.run {
                            UserData(
                                userId = uid,
                                username = trimEmail(email),
                                savedMaps = emptyList()
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
            .addOnFailureListener { exception ->
                onComplete(
                    SignInResult(
                    null,
                    errorMessage = exception.message
                )
                )
            }
    }

    fun signInWithEmail(email: String?, password: String?, onComplete: (SignInResult) -> Unit){
        if (email == null || password == null ) {
            val e = Exception()
            e.printStackTrace()
            throw e
        }
        else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    val result = if (task.isSuccessful) {
                        val user = auth.currentUser
                        SignInResult(
                            data = user?.run {
                                UserData(
                                    userId = uid,
                                    username = trimEmail(email),
                                    savedMaps = emptyList()
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
}