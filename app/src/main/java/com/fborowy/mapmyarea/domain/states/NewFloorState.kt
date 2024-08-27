package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.classes.RoomData

data class NewFloorState(
    val level: Int? = null,
    val rooms: List<RoomData> = emptyList(),
)
