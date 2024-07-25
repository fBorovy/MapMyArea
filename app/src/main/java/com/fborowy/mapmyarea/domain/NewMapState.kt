package com.fborowy.mapmyarea.domain

import com.google.android.gms.maps.model.LatLngBounds

data class NewMapState(
    var bounds: LatLngBounds? = null
)
