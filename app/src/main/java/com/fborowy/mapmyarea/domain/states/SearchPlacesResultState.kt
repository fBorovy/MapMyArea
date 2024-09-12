package com.fborowy.mapmyarea.domain.states

import com.google.android.gms.maps.model.LatLng

data class SearchPlacesResultState(
    val results: List<SearchPlacesResult> = emptyList()
)

data class SearchPlacesResult(
    val name: String,
    val location: LatLng,
    val description: String? = null,
    val buildingName: String? = null,
    val level: Int? = null,
)
