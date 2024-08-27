package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.Repository
import com.fborowy.mapmyarea.data.classes.FloorData
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.data.classes.RoomData
import com.fborowy.mapmyarea.data.classes.UserData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.states.AddingMapState
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.states.SignInState
import com.fborowy.mapmyarea.domain.trimEmail
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class AppViewModel: ViewModel() {

    private val repository: Repository = Repository()
    val database = Firebase.firestore
    val auth = Firebase.auth
    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()
    private var _userData = MutableStateFlow(UserData(userId = null, username = null, savedMaps = emptyList()))
    val userData: StateFlow<UserData> = _userData
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    private var displayedMap = MapData(null, null, "", null, null, null)
    private val maxMapNameLength = 50
    private val _addingMapState = MutableStateFlow(AddingMapState())
    val addingMapState: StateFlow<AddingMapState> = _addingMapState
    private val _mapDeletionIssue = MutableStateFlow(true)
    val mapDeletionIssue: StateFlow<Boolean> = _mapDeletionIssue
    private var _collectingUserInfoError = MutableStateFlow("")
    val collectingUserInfoError: StateFlow<String?> = _collectingUserInfoError

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
                            0 -> MarkerType.BUILDING
                            1 -> MarkerType.PARKING
                            else -> MarkerType.OTHER
                        }

                        if (markerType == MarkerType.BUILDING) {
                            val floorsSnapshot = markerDocument.reference.collection("floors")
                                .get().await()
                            val floorsList = mutableListOf<FloorData>()

                            for (floorDocument in floorsSnapshot) {
                                if (floorDocument.exists()) {
                                    val floorLevel = floorDocument.getLong("level")!!.toInt()
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
    fun switchDisplayedMap(map: MapData) {
        displayedMap = map
    }
    fun getDisplayedMapInfo(): MapData {
        return displayedMap
    }
    fun updateSearchMapText(text: String) {
        if (text.length < maxMapNameLength)
            _searchText.update { text }
        else _searchText.update { text.take(maxMapNameLength) }
    }
//    fun addMapToUserSavedMaps() {
//        if (
//            _searchText.value.length < MIN_MAP_NAME_LENGTH ||
//            FORBIDDEN_MAP_NAME_CHARACTERS.any { _searchText.value.contains(it) } ||
//            _searchText.value.startsWith(".") || _searchText.value.endsWith(".") ||
//            _searchText.value.contains("..")
//        ) {
//            _addingMapState.update {
//                AddingMapState(false, R.string.map_not_found)
//            }
//            return
//        }
//        for (map in _userData.value.savedMaps!!) {
//            if (map.mapName == _searchText.value) {
//                _addingMapState.update {
//                    AddingMapState(false, R.string.you_have_added_this_map_already)
//                }
//                return
//            }
//        }
//        viewModelScope.launch {
//            val addingMapState = repository.saveMapForUser(searchText.value)
//            if (addingMapState.addingSuccessful) {
//                _userData.update { it.copy(
//                    savedMaps = it.savedMaps!!.plus(addingMapState.addedMap!!)
//                ) }
//            } else {
//                _addingMapState.update {
//                    AddingMapState(false, addingMapState.errorCode)
//                }
//            }
//        }
//    }


    fun resetAddingMapState() {
        _addingMapState.update {
            AddingMapState(false, null)
        }
    }
    fun clearSearchText() {
        _searchText.update { "" }
    }

    fun deleteMap(mapName: String) {
        repository.deleteOwnMap(mapName)
    }

//    fun removeMapFromUserSaved(mapName: String) {
//        viewModelScope.launch {
//            try {
//                repository.removeMapFromUserMaps(mapName)
//            } catch (e: Exception) {
//                _mapDeletionIssue.update { true }
//            }
//        }
//    }

    fun resetMapDeletionStatus() {
        _mapDeletionIssue.update { false }
    }
    private fun geoPointToLatLng(geoPoint: GeoPoint): LatLng {
        return LatLng(geoPoint.latitude, geoPoint.longitude)
    }

}