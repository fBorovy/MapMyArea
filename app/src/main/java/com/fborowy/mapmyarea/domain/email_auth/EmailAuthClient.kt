package com.fborowy.mapmyarea.domain.email_auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.UserData
import com.fborowy.mapmyarea.domain.SignInResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CancellationException

class EmailAuthClient(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
): ViewModel() {
    fun signUpWithEmail(email: String, password: String, username: String, onComplete: (SignInResult) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val result = if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newFirestoreUser = hashMapOf(
                        "username" to username,
                        "savedMaps" to null,
                    )
                    Log.d("FIRE", "${user?.uid}")
                    database.collection("users")
                        .document(user!!.uid)
                        .set(newFirestoreUser)
                        .addOnSuccessListener { Log.d("AAAA", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("AAAA", "Error writing document", e) }

                    SignInResult(
                        data = user.run {
                            UserData(
                                userId = uid,
                                username = username,
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