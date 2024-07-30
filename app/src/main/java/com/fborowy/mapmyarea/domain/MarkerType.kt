package com.fborowy.mapmyarea.domain

import com.fborowy.mapmyarea.R

enum class MarkerType(val stringResource: Int, val painterResource: Int) {
    Building(R.string.building_marker_display_name, R.drawable.marker_building),
    Parking(R.string.parking_marker_display_name, R.drawable.marker_parking),
    Other(R.string.other_marker_display_name, R.drawable.marker_other),
}