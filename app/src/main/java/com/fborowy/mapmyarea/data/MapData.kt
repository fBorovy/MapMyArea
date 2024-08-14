package com.fborowy.mapmyarea.data

import com.google.firebase.firestore.GeoPoint

data class MapData(
    val mapId: String? = null,
    val mapName: String?,
    val mapDescription: String?,
    val northEastBound: GeoPoint?,
    val southWestBound: GeoPoint?,
    val markers: List<Marker>?,
)