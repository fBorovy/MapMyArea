package com.fborowy.mapmyarea.domain

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.MapData
import com.fborowy.mapmyarea.data.UserData
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

//    fun getSignedUserInfo(onComplete: (UserData) -> Unit) {
//        return if (signedWithGoogle) getSignedGoogleUserInfo{ onComplete(it) }
//        else getSignedEmailUserInfo { onComplete(it) }
//    }
    suspend fun getSignedUserInfo(): UserData {
        return if (signedWithGoogle) getSignedGoogleUserInfo()
        else getSignedEmailUserInfo()
    }

//    private fun getSignedGoogleUserInfo(onComplete: (UserData) -> Unit) {
//        val savedMaps = mutableListOf<MapData>()
//        database.collection("users")
//            .document(auth.currentUser!!.uid)
//            .collection("savedMaps")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    savedMaps.add(
//                        MapData(
//                            mapId = document.id,
//                            northLimit = document.getString("northLimit"),
//                            westLimit = document.getString("westLimit"),
//                            southLimit = document.getString("southLimit"),
//                            eastLimit = document.getString("eastLimit"),
//                        )
//                    )
//                }
//                val userData =
//                    UserData(
//                        userId = auth.currentUser?.uid,
//                        username = auth.currentUser?.displayName,
//                        savedMaps = savedMaps
//                    )
//                onComplete(userData)
//            }
//    }
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

        val snapshot = database.collection("users")
            .document(auth.currentUser!!.uid)

        val user = snapshot.get().await()
        val username = user.getString("username")

        val result = snapshot.collection("savedMaps")
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
            username = username,
            savedMaps = savedMaps
        )

    }
//    private fun getSignedEmailUserInfo(): {
//        val savedMaps = mutableListOf<MapData>()
//        var username: String?
//
//        database.collection("users")
//            .document(auth.currentUser!!.uid)
//            .get()
//            .addOnSuccessListener { snapshot ->
//                username = snapshot.getString("username")
//
//                database.collection("users")
//                    .document(auth.currentUser!!.uid)
//                    .collection("savedMaps")
//                    .get()
//                    .addOnSuccessListener { maps ->
//                        for (document in maps.documents) {
//                            savedMaps.add(
//                                MapData(
//                                    mapId = document.id,
//                                    northLimit = document.getString("northLimit"),
//                                    westLimit = document.getString("westLimit"),
//                                    southLimit = document.getString("southLimit"),
//                                    eastLimit = document.getString("eastLimit"),
//                                )
//                            )
//                        }
//                        val userData = UserData(
//                                userId = auth.currentUser?.uid,
//                                username = username?: "",
//                                savedMaps = savedMaps
//                            )
//                        onComplete(userData)
//                    }
//            }
//    }
}