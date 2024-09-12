package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.states.SearchPlacesResult
import com.fborowy.mapmyarea.domain.states.SearchPlacesResultState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MapViewModel(val map: MapData): ViewModel() {

    private var _currentMarkerInfo = MutableStateFlow(MarkerData(null, null, MarkerType.OTHER, LatLng(0.0,0.0)))
    val currentMarkerInfo: StateFlow<MarkerData> = _currentMarkerInfo
    private var _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    private val maxSearchTextLength = 50
    private var _searchResult = MutableStateFlow(SearchPlacesResultState())
    val searchResult: StateFlow<SearchPlacesResultState> = _searchResult
    var selectedLevel = MutableStateFlow(170)

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

}