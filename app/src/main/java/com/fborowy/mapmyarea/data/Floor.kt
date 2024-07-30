package com.fborowy.mapmyarea.data

data class Floor(
    val floorId: String,
    val level: Int?,
    val description: String?,
    val rooms: List<Room>,
)
