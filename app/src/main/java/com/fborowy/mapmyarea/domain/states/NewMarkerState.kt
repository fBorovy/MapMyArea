package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.classes.FloorData
import com.fborowy.mapmyarea.domain.MarkerType
import com.google.android.gms.maps.model.LatLng

data class NewMarkerState(
    val coordinates: LatLng? = null,
    val markerName: String? = "",
    val markerDescription: String? = "",
    val type: MarkerType? = null,
    val photos: String? = "",
    val floors: List<FloorData> = listOf(FloorData(level = 0, rooms = emptyList())),
)