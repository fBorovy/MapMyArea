package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.classes.MapData

data class AddingMapState(
    val addingSuccessful: Boolean = false,
    val errorCode: Int? = null,
    val addedMap: MapData? = null,
)