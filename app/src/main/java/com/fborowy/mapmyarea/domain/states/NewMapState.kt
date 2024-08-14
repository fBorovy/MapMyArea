package com.fborowy.mapmyarea.domain.states

import com.fborowy.mapmyarea.data.Marker
import com.google.android.gms.maps.model.LatLngBounds

data class NewMapState(
    var bounds: LatLngBounds? = null,
    var name: String = "",
    var description: String = "",
    var markers: List<Marker> = emptyList(),
)
