package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.Floor
import com.fborowy.mapmyarea.domain.MarkerType
import com.google.android.gms.maps.model.LatLng

data class NewMarkerState(
    val markerName: String? = "",
    val markerDescription: String?= "",
    val type: MarkerType? = null,
    val localisation: LatLng? = null,
    val photos: String? = "",
    val floors: List<Floor>? = null,
)
