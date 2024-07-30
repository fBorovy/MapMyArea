package com.fborowy.mapmyarea.domain.states

import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker

data class NewMapState(
    var bounds: LatLngBounds? = null,
    var name: String? = null,
    var description: String? = null,
    var markers: List<Marker> = emptyList(),
)
