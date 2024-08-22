package com.fborowy.mapmyarea.domain.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.domain.MarkerType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MapViewModel: ViewModel() {

    private var _currentMarkerInfo = MutableStateFlow(MarkerData(null, null, MarkerType.OTHER, LatLng(0.0,0.0)))
    val currentMarkerInfo: StateFlow<MarkerData> = _currentMarkerInfo

    fun switchMarker(marker: MarkerData) {
        Log.d("VM XDD", "${marker.markerName}")
        _currentMarkerInfo.update { it.copy(
            markerName = marker.markerName,
            markerDescription = marker.markerDescription,
            type = marker.type,
            localisation = marker.localisation,
            floors = marker.floors
        ) }
    }


}