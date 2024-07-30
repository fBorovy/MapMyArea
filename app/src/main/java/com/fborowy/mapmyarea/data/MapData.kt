package com.fborowy.mapmyarea.data

data class MapData(
    val mapId: String,
    val mapName: String?,
    val northLimit: String?,
    val westLimit: String?,
    val southLimit: String?,
    val eastLimit: String?,
    val markers: List<Marker>
)