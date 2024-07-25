package com.fborowy.mapmyarea.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapCreatorViewModel: ViewModel() {

    //funkcja korzystająca ze wzoru Haversine'a do obliczenia odległości między dwoma punktami na kuli ziemskiej
    private fun calculateDistance(c1Lat: Double, c1Lon: Double, c2Lat: Double, c2Lon: Double): Double {
        val earthRadius = 6371.0

        val deltaLatitude = if (c2Lat > c1Lat) Math.toRadians(c2Lat - c1Lat)
        else Math.toRadians(c1Lat - c2Lat)
        val deltaLongitude = if (c2Lon > c1Lon) Math.toRadians(c2Lon - c1Lon)
        else Math.toRadians(c1Lon - c2Lon)

        val haversineFactor = sin(deltaLongitude / 2) * sin(deltaLatitude / 2) + cos(Math.toRadians(c1Lat)) * cos(Math.toRadians(c2Lat)) * sin(deltaLongitude / 2) * sin(deltaLongitude / 2)
        val centralAngle = 2 * atan2(sqrt(haversineFactor), sqrt(1 - haversineFactor))

        val distance = earthRadius * centralAngle
        Log.d("DISTANCE", "${deltaLongitude}, ${deltaLatitude}")
        return distance
    }

    fun isAreaWithinLimit(maxDiagonalLengthInKilometers: Double, corner1: LatLng?, corner2: LatLng?): Boolean {
        if (corner1 == null || corner2 == null) return false
        Log.d("C1", "${corner1}")
        Log.d("C2", "${corner2}")

        val distance = calculateDistance(
            corner1.latitude, corner1.longitude,
            corner2.latitude, corner2.longitude
        )
        Log.d("DISTANCE", "${distance}, ${maxDiagonalLengthInKilometers}")
        return distance < maxDiagonalLengthInKilometers
    }

    private var newMapState = MutableStateFlow(NewMapState())
    fun setBoundaries(corner1: LatLng?, corner2: LatLng?) {
        val boundSW = LatLng(
            minOf(corner1!!.latitude, corner2!!.latitude),
            minOf(corner1.longitude, corner2.longitude)
        )

        val boundNE = LatLng(
            maxOf(corner1.latitude, corner2.latitude),
            maxOf(corner1.longitude, corner2.longitude)
        )
        val bounds = LatLngBounds(boundSW, boundNE)
        newMapState.value.bounds = bounds
    }

    fun getBoundaries(): LatLngBounds? {
        return newMapState.value.bounds
    }
}