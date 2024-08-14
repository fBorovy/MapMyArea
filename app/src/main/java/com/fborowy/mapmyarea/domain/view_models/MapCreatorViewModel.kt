package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import com.fborowy.mapmyarea.data.Floor
import com.fborowy.mapmyarea.data.MapData
import com.fborowy.mapmyarea.data.MapRepository
import com.fborowy.mapmyarea.data.Marker
import com.fborowy.mapmyarea.data.Room
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.states.NewFloorState
import com.fborowy.mapmyarea.domain.states.NewMapState
import com.fborowy.mapmyarea.domain.states.NewMarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("SameParameterValue")
class MapCreatorViewModel: ViewModel() {

    private val mapRepository: MapRepository = MapRepository()

    private var _newMapState = MutableStateFlow(NewMapState())
    val newMapState: StateFlow<NewMapState> = _newMapState
    private val _corner1position = MutableStateFlow<LatLng?>(null)
    val corner1position: StateFlow<LatLng?> = _corner1position
    private val _corner2position = MutableStateFlow<LatLng?>(null)
    val corner2position: StateFlow<LatLng?> = _corner2position
    private var _newMarkerState = MutableStateFlow(NewMarkerState())
    val newMarkerState: StateFlow<NewMarkerState> = _newMarkerState
    private var _newFloorState = MutableStateFlow(NewFloorState())
    val newFloorState: StateFlow<NewFloorState> = _newFloorState
    var isInstructionScreen1Visible = true
    var isInstructionScreen2Visible = true

    fun addFloor(onTop: Boolean) {
        if (onTop) {
            _newMarkerState.update { it.copy(
                floors = it.floors + Floor(
                    level = it.floors.last().level + 1,
                    rooms = emptyList()
                )
            )}
        }
        else {
            _newMarkerState.update { it.copy(
                floors = listOf(Floor(
                    level = it.floors.first().level - 1,
                    rooms = emptyList()
                )) + it.floors
            )}
        }
    }

    fun removeFloor(onTop: Boolean) {
        if (onTop) {
            _newMarkerState.update { it.copy(
                floors = it.floors.subList(
                    fromIndex = 0,
                    toIndex = it.floors.size - 1
                )
            ) }
        }
        else {
            _newMarkerState.update { it.copy(
                floors = it.floors.subList(
                    fromIndex = 1,
                    toIndex = it.floors.size
                )
            ) }
        }
    }
    fun addRoomToFloor(room: Room) {
        _newMarkerState.update { currentState ->
            val updatedFloors = currentState.floors.map { floor ->
                if (floor.level == _newFloorState.value.level) {
                    _newFloorState.update { it.copy(
                        rooms = floor.rooms + room
                    ) }
                    floor.copy(rooms = floor.rooms + room)

                } else {
                    floor
                }
            }
            currentState.copy(floors = updatedFloors)
        }
    }
    fun removeRoomFromFloor(room: Room) {
        _newMarkerState.update { currentState ->
            val updatedFloors = currentState.floors.map {floor ->
                if (floor.level == _newFloorState.value.level) {
                    _newFloorState.update { it.copy(
                        rooms = floor.rooms - room
                    ) }
                    floor.copy(rooms = floor.rooms - room)
                } else {
                    floor
                }
            }
            currentState.copy(floors = updatedFloors)
        }
    }

    fun shiftSelectedFloor(selectedFloorLevel: Int) {
        for (floor in _newMarkerState.value.floors) {
            if (floor.level == selectedFloorLevel) {
                _newFloorState.update { it.copy(
                    level = selectedFloorLevel,
                    rooms = floor.rooms
                )}
            }
        }
    }
    fun saveNewMap() {
        mapRepository.addNewMap(
            MapData(
                mapName = _newMapState.value.name,
                mapDescription = _newMapState.value.description,
                northEastBound = GeoPoint(_newMapState.value.bounds!!.northeast.latitude, _newMapState.value.bounds!!.northeast.longitude),
                southWestBound = GeoPoint(_newMapState.value.bounds!!.southwest.latitude, _newMapState.value.bounds!!.southwest.longitude),
                markers = _newMapState.value.markers
            )
        )
    }

    fun setNewMapName(name: String) {
        _newMapState.value = _newMapState.value.copy(name = name)
    }

    fun setNewMapDescription(description: String) {
        _newMapState.value = _newMapState.value.copy(description = description)
    }


    fun setNewMarkerCoordinates(point: LatLng) {
        _newMarkerState.value = _newMarkerState.value.copy(
            coordinates = point
        )
    }

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
                _newMapState.value.bounds = bounds
            }
            else {
                _corner1position.value = null
                _corner2position.value = null
                return 2
            }
        } else {
            return 1
        }
        return 0
    }

    fun getBoundaries(): LatLngBounds? {
        return _newMapState.value.bounds
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
        _newMapState.value.bounds = null
    }

    fun setMarkerNameDescription(markerName: String, markerDescription: String) {
        _newMarkerState.update {
            _newMarkerState.value.copy(
                markerName = markerName,
                markerDescription = markerDescription,
            )
        }
    }

    fun setMarkerType(markerType: MarkerType) {
        _newMarkerState.update {
            _newMarkerState.value.copy(
                type = markerType
            )
        }
    }

    fun addNewMarkerToMap() {
        _newMapState.update {it.copy(
                markers = it.markers.plus(Marker(
                    localisation = _newMarkerState.value.coordinates!!,
                    markerName = _newMarkerState.value.markerName,
                    markerDescription = _newMarkerState.value.markerDescription,
                    photos = "",
                    type = _newMarkerState.value.type!!
                ))
            )
        }
        resetNewMarkerState()
    }

    fun getMarkerType(): MarkerType {
        return _newMarkerState.value.type!!
    }

    fun resetNewMarkerState() {
        _newMarkerState.value = _newMarkerState.value.copy(
            coordinates = null,
            markerName = "",
            markerDescription = "",
            photos = "",
            floors = listOf(Floor(level = 0, rooms = emptyList())),
        )
    }
}