package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.classes.FloorData
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.data.classes.RoomData
import com.fborowy.mapmyarea.data.classes.UserData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.states.SignInState
import com.fborowy.mapmyarea.domain.trimEmail
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class AppViewModel: ViewModel() {

    val auth = Firebase.auth
    private val _signInState = MutableStateFlow(SignInState())
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
        val savedMaps = mutableListOf<MapData>()
        val userDocumentReference = database.collection("users")
            .document(auth.currentUser!!.uid)

        val userSavedMapsSnapshot = userDocumentReference.collection("savedMaps")
            .get()
            .await()

        for (savedMapDocumentReference in userSavedMapsSnapshot) {
            val mapReference = savedMapDocumentReference.getDocumentReference("mapRef")

            if (mapReference != null) {
                val mapDocument = mapReference.get().await()

                if (mapDocument.exists()) {
                    val mapName = mapDocument.getString("name")
                    val mapDescription = mapDocument.getString("description")
                    val northEastBound = mapDocument.getGeoPoint("northEastBound")
                    val southWestBound = mapDocument.getGeoPoint("southWestBound")
                    val markersSnapshot = mapDocument.reference.collection("markers")
                        .get().await()
                    val markersList = mutableListOf<MarkerData>()

                    for (markerDocument in markersSnapshot) {
                        val markerName = markerDocument.getString("name")
                        val markerDescription = markerDocument.getString("description")
                        val markerCoordinates = markerDocument.getGeoPoint("coordinates")
                        val mType = markerDocument.getLong("type")!!.toInt()
                        val markerType = when (mType) {
                            0 -> MarkerType.Building
                            1 -> MarkerType.Parking
                            else -> MarkerType.Other
                        }

                        if (markerType == MarkerType.Building) {
                            val floorsSnapshot = markerDocument.reference.collection("floors")
                                .get().await()
                            val floorsList = mutableListOf<FloorData>()

                            for (floorDocument in floorsSnapshot) {
                                if (floorDocument.exists()) {
                                    val floorId = floorDocument.id
                                    val floorLevel = floorDocument.getLong("level")!!.toInt()
                                    val floorDescription = floorDocument.getString("description")
                                    val roomsSnapshot = floorDocument.reference.collection("rooms")
                                        .get().await()
                                    val roomsList = mutableListOf<RoomData>()

                                    for (roomDocument in roomsSnapshot) {
                                        if (roomDocument.exists()) {
                                            val roomName = roomDocument.getString("name")
                                            val roomDescription = roomDocument.getString("description")

                                            val room = RoomData(
                                                name = roomName!!,
                                                description = roomDescription!!
                                            )
                                            roomsList.add(room)
                                        }
                                    }

                                    val floor = FloorData(
                                        level = floorLevel,
                                        rooms = roomsList
                                    )
                                    floorsList.add(floor)
                                }
                            }
                            val marker = MarkerData(
                                markerName = markerName,
                                markerDescription = markerDescription,
                                type = markerType,
                                localisation = geoPointToLatLng(markerCoordinates!!),
                                photos = null,
                                floors = floorsList
                            )
                            markersList.add(marker)
                        }
                        val map = MapData(
                            mapName = mapName,
                            owner = auth.currentUser!!.uid,
                            mapDescription = mapDescription?: "",
                            northEastBound = northEastBound,
                            southWestBound = southWestBound,
                            markers = markersList
                        )
                        savedMaps.add(map)
                    }
                }
            }
        }
        return UserData(
            userId = auth.currentUser?.uid,
            username = trimEmail(auth.currentUser!!.email!!),
            savedMaps = savedMaps
        )
    }

    private fun geoPointToLatLng(geoPoint: GeoPoint): LatLng {
        return LatLng(geoPoint.latitude, geoPoint.longitude)
    }

}