package com.fborowy.mapmyarea.data

import android.util.Log
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.FloorData
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.data.classes.RoomData
import com.fborowy.mapmyarea.data.classes.UserData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.states.AddingMapState
import com.fborowy.mapmyarea.domain.states.SavingMapState
import com.fborowy.mapmyarea.domain.trimEmail
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class Repository (
    private val database: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth = Firebase.auth
) {

    fun checkIfLogged(): Boolean {
        return (auth.currentUser != null)
    }
    suspend fun getSignedUserInfo(): UserData? {
        return try {
            val userDocumentReference = database.collection("users").document(auth.currentUser!!.email!!)
            val userDocumentSnapshot = userDocumentReference.get().await()

            if (userDocumentSnapshot.exists()) {
                var savedMaps = listOf<MapData>()
                val savedMapsPaths: List<String>? = userDocumentSnapshot.get("savedMaps") as List<String>?
                if (savedMapsPaths != null) {
                    for (mapPath in savedMapsPaths) {
                        val map = getMapFromMapPath(mapPath)
                        if (map != null) {
                            savedMaps = savedMaps + map
                        }
                    }
                }
                UserData(
                    userId = auth.currentUser!!.email!!,
                    username = trimEmail(auth.currentUser!!.email!!),
                    savedMaps = savedMaps
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun addEditMap(map: MapData, isMapEdited: Boolean): SavingMapState {
        val docRef = database.collection("maps").document(map.mapName!!)
        val mapDocument = docRef.get().await()
        if (mapDocument.exists() && !isMapEdited) {
            return SavingMapState(false, "exists")
        }

        val markers = mutableListOf<Map<String, Any?>>()
        if (map.markers != null) {
            for (marker in map.markers) {
                val floors = mutableListOf<Map<String, Any?>>()
                for (floor in marker.floors) {
                    val rooms = mutableListOf<Map<String, Any?>>()
                    for (room in floor.rooms) {
                        rooms.add(mapOf<String, Any?>(
                            "name" to room.name,
                            "description" to room.description,
                        ))
                    }
                    floors.add(mapOf(
                        "level" to floor.level,
                        "rooms" to rooms
                    ))
                }
                markers.add(mapOf(
                    "name" to marker.markerName,
                    "description" to marker.markerDescription,
                    "coordinates" to marker.localisation,
                    "type" to marker.type.string,
                    "floors" to floors
                ))
            }
        }

        val newMap = mapOf(
            "description" to map.mapDescription,
            "markers" to markers,
            "northEastBound" to  map.northEastBound,
            "southWestBound" to map.southWestBound,
            "owner" to auth.currentUser!!.email
        )
        var returnValue = SavingMapState(true, null)

        docRef.set(newMap)
            .addOnFailureListener {
                returnValue = SavingMapState(false, it.message)
            }.await()

        try {
            val userDocumentReference = database.collection("users").document(auth.currentUser!!.email!!)
            userDocumentReference.update("savedMaps", FieldValue.arrayUnion("/maps/${map.mapName}")).await()
        } catch (e: Exception) {
            returnValue = SavingMapState(false, e.message)
        }
        return returnValue
    }

    suspend fun saveMapForUser(mapId: String): AddingMapState {
        val mapDocumentSnapshot = database.collection("maps").document(mapId).get().await()
        if (!(mapDocumentSnapshot.exists())) {
            return AddingMapState(false, R.string.map_not_found)
        }
        return try {
            val userDocumentReference = database.collection("users").document(auth.currentUser!!.email!!)
            userDocumentReference.update("savedMaps", FieldValue.arrayUnion("/maps/${mapId}"))
                .await()
            AddingMapState(true, null, getMapFromMapPath("/maps/${mapId}"))
        } catch (e: Exception) {
            AddingMapState(false, R.string.unknown_error)
        }
    }


    fun deleteOwnMap(mapId: String) {
        try {
            database.collection("maps").document(mapId).delete()
        } catch (e: Exception) {
            throw e
        }
    }

    fun removeMapFromUserMaps(mapPath: String) {
        try {
            val userDocumentReference = database.collection("users").document(auth.currentUser!!.email!!)
            userDocumentReference.update(
                "savedMaps",
                FieldValue.arrayRemove(mapPath)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getMapFromMapPath(mapPath: String): MapData? {
        val mapDocumentSnapshot = database.document(mapPath).get().await()
        if (!mapDocumentSnapshot.exists()) {
            removeMapFromUserMaps(mapPath)
            return null
        }
        Log.d("XD", mapDocumentSnapshot.get("markers").toString())
        val markers = mutableListOf<MarkerData>()
        val markersSnapshot = mapDocumentSnapshot.get("markers") as ArrayList<*>
        Log.d("XD", markersSnapshot.toString())
        for (marker in markersSnapshot) {
            val markerMap = marker as HashMap<*, *>
            val coordinates = markerMap["coordinates"] as? HashMap<*, *>
            val latitude = coordinates?.get("latitude") as Double
            val longitude = coordinates["longitude"] as Double
            val localisation = LatLng(latitude, longitude)
            val floors = mutableListOf<FloorData>()
            val floorsSnapshot = markerMap["floors"] as ArrayList<*>
            for (floor in floorsSnapshot) {
                val floorMap = floor as HashMap<*, *>
                val rooms = mutableListOf<RoomData>()
                val roomsSnapshot = floorMap["rooms"] as ArrayList<*>
                for (room in roomsSnapshot) {
                    val roomMap = room as HashMap<*, *>
                    rooms.add(RoomData(
                        name = roomMap["name"] as String,
                        description = roomMap["description"] as String
                    ))
                }
                floors.add(FloorData(
                        level = (floorMap["level"] as Long).toInt(),
                        rooms = rooms
                ))
            }
            markers.add(MarkerData(
                    markerName = markerMap["name"] as String,
                    localisation = localisation,
                    markerDescription = markerMap["description"] as String,
                    type = MarkerType.fromString(markerMap["type"] as String)!!,
                    floors = floors
            ))
        }
        return MapData(
            mapName = mapDocumentSnapshot.id,
            mapDescription = mapDocumentSnapshot.getString("mapDescription")?:"",
            markers = markers,
            owner = mapDocumentSnapshot.getString("owner"),
            northEastBound = mapDocumentSnapshot.getGeoPoint("northEastBound"),
            southWestBound = mapDocumentSnapshot.getGeoPoint("southWestBound"),
        )
    }

    fun addUserDataToFirestoreIfItsNotThere() {
        val user = auth.currentUser
        database.collection("users")
            .document(user!!.email!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (!(document.exists())) {
                        val newFirestoreUser = hashMapOf(
                            "username" to trimEmail(user.email!!),
                            "savedMaps" to null,
                        )
                        database.collection("users")
                            .document(user.email!!)
                            .set(newFirestoreUser)
                    }
                }
            }
    }
}