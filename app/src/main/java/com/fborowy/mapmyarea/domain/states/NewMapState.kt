package com.fborowy.mapmyarea.domain.states

import com.google.android.gms.maps.model.LatLngBounds

data class NewMapState(
    var bounds: LatLngBounds? = null
)
