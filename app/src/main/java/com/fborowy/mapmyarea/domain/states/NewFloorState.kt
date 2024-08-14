package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.Room

data class NewFloorState(
    val level: Int? = null,
    val rooms: List<Room> = emptyList(),
)
