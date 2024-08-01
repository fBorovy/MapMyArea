package com.fborowy.mapmyarea.data

data class Floor(
    val floorId: String,
    val level: Int?,
    val rooms: List<Room>,
)
