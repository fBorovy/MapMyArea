package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.domain.states.NewMapState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("SameParameterValue")
class MapCreatorViewModel: ViewModel() {

    private var newMapState = MutableStateFlow(NewMapState())
    private val _corner1position = MutableStateFlow<LatLng?>(null)
    val corner1position: StateFlow<LatLng?> = _corner1position
    private val _corner2position = MutableStateFlow<LatLng?>(null)
    val corner2position: StateFlow<LatLng?> = _corner2position

    fun setCorner(latLng: LatLng) {
        if (_corner1position.value == null) {
            _corner1position.value = latLng
        } else if (corner2position.value == null) {
            _corner2position.value = latLng
        }
    }

    fun resetCorners() {
        _corner1position.value = null
        _corner2position.value = null
    }
    fun setBoundaries(): Int {
        if (_corner1position.value != null && _corner2position.value != null) {
            if (isAreaWithinLimit(3.0, _corner1position.value, _corner2position.value)) {
                val boundSW = LatLng(
                    minOf(_corner1position.value!!.latitude, _corner2position.value!!.latitude),
                    minOf(_corner1position.value!!.longitude, _corner2position.value!!.longitude)
                )
                val boundNE = LatLng(
                    maxOf(_corner1position.value!!.latitude, _corner2position.value!!.latitude),
                    maxOf(_corner1position.value!!.longitude, _corner2position.value!!.longitude)
                )
                val bounds = LatLngBounds(boundSW, boundNE)
                newMapState.value.bounds = bounds
            }
            else {
                return 2
            }
        } else {
            return 1
        }
        return 0
    }

    fun getBoundaries(): LatLngBounds? {
        return newMapState.value.bounds
    }

    private fun isAreaWithinLimit(maxDiagonalLengthInKilometers: Double, corner1: LatLng?, corner2: LatLng?): Boolean {
        if (corner1 == null || corner2 == null) return false
        val distance = calculateDistance(
            corner1.latitude, corner1.longitude,
            corner2.latitude, corner2.longitude
        )
        return distance < maxDiagonalLengthInKilometers
    }

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

        return distance
    }

    fun resetNewMapState() {
        resetCorners()
        newMapState.value.bounds = null
    }
}