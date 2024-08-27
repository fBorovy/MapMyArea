package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.classes.MarkerData
import com.google.android.gms.maps.model.LatLngBounds

data class NewMapState(
    var bounds: LatLngBounds? = null,
    var name: String = "",
    var description: String = "",
    var markers: List<MarkerData> = emptyList(),
)
