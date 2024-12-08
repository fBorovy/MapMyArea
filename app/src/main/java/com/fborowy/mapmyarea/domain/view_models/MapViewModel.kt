package com.fborowy.mapmyarea.domain.view_models

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fborowy.mapmyarea.data.Repository
import com.fborowy.mapmyarea.data.classes.DirectionsResponse
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.states.SearchPlacesResult
import com.fborowy.mapmyarea.domain.states.SearchPlacesResultState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    val map: MapData,
    private val repository: Repository = Repository()
): ViewModel() {

    private var _currentMarkerInfo = MutableStateFlow(MarkerData(null, null, MarkerType.OTHER, LatLng(0.0,0.0)))
    val currentMarkerInfo: StateFlow<MarkerData> = _currentMarkerInfo
    private var _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    private val maxSearchTextLength = 50
    private var _searchResult = MutableStateFlow(SearchPlacesResultState())
    val searchResult: StateFlow<SearchPlacesResultState> = _searchResult
    var selectedLevel = MutableStateFlow(170)
    private var _routes = MutableStateFlow<Pair<DirectionsResponse, DirectionsResponse>?>(null)
    val routes: StateFlow<Pair<DirectionsResponse, DirectionsResponse>?> = _routes
    private var _walkingPolylinePath = MutableStateFlow<ArrayList<LatLng>?>(null)
    val walkingPolylinePath: StateFlow<ArrayList<LatLng>?> = _walkingPolylinePath
    private var _drivingPolylinePath = MutableStateFlow<ArrayList<LatLng>?>(null)
    val drivingPolylinePath: StateFlow<ArrayList<LatLng>?> = _drivingPolylinePath


    fun switchMarker(marker: MarkerData? = null, location: LatLng? = null, level: Int? = null) {
        if (location != null) {
            for (foundMarker in map.markers!!) {
                if (foundMarker.localisation == location) {
                    _currentMarkerInfo.update { it.copy(
                        markerName = foundMarker.markerName,
                        markerDescription = foundMarker.markerDescription,
                        type = foundMarker.type,
                        localisation = foundMarker.localisation,
                        floors = foundMarker.floors
                    ) }
                    if (level != null)
                        selectedLevel.value = level
                    return
                }
            }
        }
        if (marker != null)
            _currentMarkerInfo.update { it.copy(
                markerName = marker.markerName,
                markerDescription = marker.markerDescription,
                type = marker.type,
                localisation = marker.localisation,
                floors = marker.floors
            ) }
    }

    fun resetMarker() {
        _currentMarkerInfo.update {
            MarkerData(null, null, MarkerType.OTHER, LatLng(0.0,0.0))
        }
    }

    fun updateSearchText(newValue: String) {
        resetSearchResult()
        if (searchText.value.length > maxSearchTextLength) {
            _searchText.value = newValue.take(maxSearchTextLength)
        } else
            _searchText.value = newValue

        if (searchText.value.length > 2)
            searchPlaces()
    }

    fun tintBitmap(original: Bitmap, color: Int): Bitmap {
        val tintedBitmap = Bitmap.createBitmap(original.width, original.height, original.config)
        val canvas = Canvas(tintedBitmap)
        val paint = Paint().apply {
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(original, 0f, 0f, paint)
        return tintedBitmap
    }

    private fun searchPlaces() {
        if (map.markers.isNullOrEmpty()) return
        for (marker in map.markers) {
            if (marker.markerName!!.lowercase().contains(_searchText.value.lowercase()))
                _searchResult.update { it.copy(
                    results = it.results.plus(
                        SearchPlacesResult(
                            name = marker.markerName,
                            description = marker.markerDescription,
                            location = marker.localisation,
                        )
                    ),
                ) }
            if (marker.type == MarkerType.BUILDING) {
                for (floor in marker.floors) {
                    for (room in floor.rooms) {
                        if (room.name.lowercase().contains(_searchText.value.lowercase()))
                            _searchResult.update { it.copy(
                                results = it.results.plus(
                                    SearchPlacesResult(
                                        name = room.name,
                                        description = room.description,
                                        location = marker.localisation,
                                        buildingName = marker.markerName,
                                        level = floor.level
                                    )
                                ),
                            ) }
                    }
                }
            }
        }
    }

    fun resetSearchText() {
        _searchText.value = ""
    }
    fun resetSearchResult() {
        _searchResult.value = SearchPlacesResultState()
    }

    fun getRoutes(origin: LatLng, destination: LatLng, apiKey: String?) {
        if (apiKey != null) {
            val originString = "${origin.latitude},${origin.longitude}"
            val destinationString = "${destination.latitude},${destination.longitude}"
            viewModelScope.launch {
                _routes.value = repository.getRoutes(originString, destinationString, apiKey)
                Log.d("vm routes", _routes.value?.first!!.routes[0].overviewPolyline?.points ?: "")
            }
        } else {
            Log.d("api", "api key is null")
        }
    }

    fun decodeOverviewPolyline(encoded: String?, driving: Boolean): Boolean {
        if (encoded == null) return false
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dLat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dLng

            val point = LatLng(
                lat / 1E5,
                lng / 1E5
            )
            poly.add(point)
        }
        if (driving) {
            _drivingPolylinePath.value = poly
        } else {
            _walkingPolylinePath.value = poly
        }
        Log.d("path array", _drivingPolylinePath.value.toString())
        return true
    }

    fun clearRoutes() {
        _walkingPolylinePath.value = null
        _drivingPolylinePath.value = null
        _routes.value = null
    }
}