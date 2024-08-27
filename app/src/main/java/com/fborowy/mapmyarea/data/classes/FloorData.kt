package com.fborowy.mapmyarea.data.classes

data class FloorData(
    val level: Int,
    val rooms: List<RoomData> = emptyList()
)
