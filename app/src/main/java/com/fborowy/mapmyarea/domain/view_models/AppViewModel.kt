package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.MapData
import com.fborowy.mapmyarea.data.UserData
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.states.SignInState
import com.fborowy.mapmyarea.domain.trimEmail
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class AppViewModel: ViewModel() {

    val auth = Firebase.auth
    private val _signInState = MutableStateFlow(SignInState())
    private val signedWithGoogle = false
    val signInState = _signInState.asStateFlow()
    val database = Firebase.firestore

    fun onSignIn(result: SignInResult) {
        _signInState.update { it.copy(
            signInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }
    fun resetSignInState() {
        _signInState.update { SignInState() }
    }

    suspend fun getSignedUserInfo(): UserData {
        return if (signedWithGoogle) getSignedGoogleUserInfo()
        else getSignedEmailUserInfo()
    }

    private suspend fun getSignedGoogleUserInfo(): UserData {
        val savedMaps = mutableListOf<MapData>()
        val snapshot = database.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("savedMaps")
            .get()
            .await()

        for (document in snapshot) {
            savedMaps.add(
                MapData(
                    mapId = document.id,
                    northLimit = document.getString("northLimit"),
                    westLimit = document.getString("westLimit"),
                    southLimit = document.getString("southLimit"),
                    eastLimit = document.getString("eastLimit"),
                )
            )
        }

        return UserData(
            userId = auth.currentUser?.uid,
            username = auth.currentUser?.displayName,
            savedMaps = savedMaps
        )
    }
    private suspend fun getSignedEmailUserInfo(): UserData {
        val savedMaps = mutableListOf<MapData>()

        val result = database.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("savedMaps")
            .get()
            .await()

        for (document in result) {
            savedMaps.add(
                MapData(
                    mapId = document.id,
                    northLimit = document.getString("northLimit"),
                    westLimit = document.getString("westLimit"),
                    southLimit = document.getString("southLimit"),
                    eastLimit = document.getString("eastLimit"),
                )
            )
        }

        return UserData(
            userId = auth.currentUser!!.uid,
            username = trimEmail(auth.currentUser!!.email!!),
            savedMaps = savedMaps
        )

    }
}