package com.fborowy.mapmyarea.data.classes

import com.google.firebase.firestore.GeoPoint

data class MapData(
    val mapName: String?, //id w firestore
    var owner: String? = null,
    val mapDescription: String = "",
    val northEastBound: GeoPoint?,
    val southWestBound: GeoPoint?,
    val markers: List<MarkerData>?,
)