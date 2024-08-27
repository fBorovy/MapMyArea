package com.fborowy.mapmyarea.data

import android.content.ContentValues.TAG
import android.util.Log
import com.fborowy.mapmyarea.data.classes.FloorData
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.data.classes.RoomData
import com.fborowy.mapmyarea.domain.MarkerType
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class Repository {

    //private val auth = Firebase.auth
    private val database = Firebase.firestore
    //private val userDocumentReference = database.collection("users").document(auth.currentUser!!.email!!)

    fun addNewMap(map: MapData) {
        val markers = mutableListOf<Map<String, Any?>>()
        if (map.markers != null) {
            for (marker in map.markers) {
                if (marker.type == MarkerType.BUILDING) {
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
                        "type" to marker.type,
                        "floors" to marker.floors
                    ))
                }
                else {
                    markers.add(mapOf(
                        "name" to marker.markerName,
                        "description" to marker.markerDescription,
                        "coordinates" to marker.localisation,
                        "type" to marker.type,
                        "floors" to null
                    ))
                }
            }
        }

        val newMap = mapOf(
            "description" to map.mapDescription,
            "mapName" to map.mapName,
            "markers" to markers,
            "northEastBound" to  map.northEastBound,
            "southWestBound" to map.southWestBound,
        )

        database.collection("maps").add(newMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener {
                e -> Log.w(TAG, "Error writing document", e)
            }

    }

//    suspend fun saveMapForUser(mapId: String): AddingMapState {
//        val mapDocumentSnapshot = database.collection("maps").document(mapId).get().await()
//        if (!(mapDocumentSnapshot.exists())) {
//            return AddingMapState(false, R.string.map_not_found)
//        }
//        return try {
//            userDocumentReference.update("savedMaps", FieldValue.arrayUnion("/maps/${mapId}"))
//                .await()
//            AddingMapState(true, null, getMapFromMapPath("/maps/${mapId}"))
//        } catch (e: Exception) {
//            AddingMapState(false, R.string.unknown_error)
//        }
//    }

    fun deleteOwnMap(mapId: String) {
        try {
            database.collection("maps").document(mapId).delete()
        } catch (e: Exception) {
            throw e
        }
    }

//    fun removeMapFromUserMaps(mapName: String) {
//        try {
//            userDocumentReference.update(
//                "savedMaps",
//                FieldValue.arrayRemove("/maps/${mapName}")
//            )
//        } catch (e: Exception) {
//            throw e
//        }
//    }

    private suspend fun getMapFromMapPath(mapPath: String): MapData {
        val mapDocumentSnapshot = database.document(mapPath).get().await()
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
}