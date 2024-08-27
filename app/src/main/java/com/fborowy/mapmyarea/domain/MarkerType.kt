package com.fborowy.mapmyarea.domain

import com.fborowy.mapmyarea.R

enum class MarkerType(val stringResource: Int, val painterResource: Int, val string: String) {
    BUILDING(R.string.building_marker_display_name, R.drawable.marker_building, "building"),
    PARKING(R.string.parking_marker_display_name, R.drawable.marker_parking, "parking"),
    OTHER(R.string.other_marker_display_name, R.drawable.marker_other, "other");

    companion object {
        fun fromString(string: String): MarkerType? {
            return entries.find {it.string == string}
        }
    }
}