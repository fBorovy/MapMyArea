package com.fborowy.mapmyarea.data

import com.google.firebase.firestore.GeoPoint

data class MapData(
    val mapId: String,
    val northLimit: String?,
    val westLimit: String?,
    val southLimit: String?,
    val eastLimit: String?,
)