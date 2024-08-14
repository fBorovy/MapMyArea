package com.fborowy.mapmyarea.data

data class Floor(
    val floorId: String? = null,
    val level: Int,
    val rooms: List<Room> = emptyList()
)
