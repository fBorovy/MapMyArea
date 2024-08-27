package com.fborowy.mapmyarea.data

import android.content.ContentValues.TAG
import android.util.Log
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.domain.MarkerType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MapRepository {

    private val database = Firebase.firestore

    fun addNewMap(map: MapData) {
        val markers = mutableListOf<Map<String, Any?>>()
        if (map.markers != null) {
            for (marker in map.markers) {
                if (marker.type == MarkerType.Building) {
                    val floors = mutableListOf<Map<String, Any?>>()
                    for (floor in marker.floors!!) {
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
}