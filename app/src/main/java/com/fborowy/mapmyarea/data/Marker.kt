package com.fborowy.mapmyarea.data

import com.fborowy.mapmyarea.domain.MarkerType
import com.google.android.gms.maps.model.LatLng

data class Marker(
    val markerId: String? = null,
    val markerName: String?,
    val markerDescription: String?,
    val type: MarkerType,
    val localisation: LatLng,
    val photos: String?,
    val floors: List<Floor>? = null,
)
