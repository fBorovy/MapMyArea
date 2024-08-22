package com.fborowy.mapmyarea.data.classes

data class FloorData(
    val floorId: String? = null,
    val level: Int,
    val rooms: List<RoomData> = emptyList()
)
