package com.fborowy.mapmyarea.data.classes

import com.fborowy.mapmyarea.domain.MarkerType
import com.google.android.gms.maps.model.LatLng

data class MarkerData(
    val markerName: String?,
    val markerDescription: String?,
    val type: MarkerType,
    val localisation: LatLng,
    val photos: String? = null,
    val floors: List<FloorData> = emptyList(),
)
