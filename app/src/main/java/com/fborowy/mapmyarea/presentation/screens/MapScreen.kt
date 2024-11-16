package com.fborowy.mapmyarea.presentation.screens

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.MarkerData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.view_models.MapViewModel
import com.fborowy.mapmyarea.presentation.MapStyle
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMATextField
import com.fborowy.mapmyarea.ui.theme.ButtonBlack
import com.fborowy.mapmyarea.ui.theme.Typography
import com.fborowy.mapmyarea.ui.theme.onMapButtonBackground
import com.fborowy.mapmyarea.ui.theme.onMapButtonText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

const val CENTER_MAP_DURATION_IN_MS = 350

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    map: MapData,
    navController: NavController
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var showMarkerInfo by rememberSaveable { mutableStateOf(false) }
    val displayedMarker by mapViewModel.currentMarkerInfo.collectAsState()
    val selectedLocation = rememberSaveable { mutableStateOf<LatLng?>(null) }
    val departureLocation = rememberSaveable { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()
    val searchText by mapViewModel.searchText.collectAsState()
    val searchResult by mapViewModel.searchResult.collectAsState()
    val fusedLocationClient: FusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    val routes by mapViewModel.routes.collectAsState()
    var driving by rememberSaveable { mutableStateOf(false) }
    val walkingPath by mapViewModel.walkingPolylinePath.collectAsState()
    val drivingPath by mapViewModel.drivingPolylinePath.collectAsState()

    val appInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )
    val bundle = appInfo.metaData

    val couldNotFindLocationToast = Toast.makeText(
        context,
        stringResource(R.string.location_could_not_be_found),
        Toast.LENGTH_SHORT,
    )

    val mapUiSettings = MapUiSettings(
        rotationGesturesEnabled = true,
        myLocationButtonEnabled = true, //isMyLocationEnabled has to be true too to display it
        compassEnabled = true,
        zoomControlsEnabled = !showMarkerInfo,
        //mapToolbarEnabled = false,
    )

    LaunchedEffect(routes, driving) {
        Log.d("launched routes", routes.toString())
        if (routes != null) {
            Log.d("routes not null", "")
            mapViewModel.decodeOverviewPolyline(routes!!.first.routes[0].overviewPolyline?.points, true)
            mapViewModel.decodeOverviewPolyline(routes!!.second.routes[0].overviewPolyline?.points, false)
        }
    }

    LaunchedEffect(Unit) {
        mapViewModel.resetMarker()
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide() // Ukrywa klawiaturę
                    focusManager.clearFocus()
                    mapViewModel.resetSearchResult()
                    mapViewModel.resetSearchText()
                })
            }
            .background(MaterialTheme.colorScheme.background),

    ) {
        Box {
            Column(
                modifier = Modifier.zIndex(1f)
            ){
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 9.dp,
                            shape = RoundedCornerShape(bottomEnd = 25.dp, bottomStart = 25.dp),
                            clip = true
                        )
                        .zIndex(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                ) {
                    MMAHeader(
                        header = map.mapName?: stringResource(id = R.string.unknown_map),
                        onGoBack = {
                            navController.popBackStack()
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .zIndex(1f)
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp, end = 60.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(onMapButtonBackground.copy(alpha = 0.75f))
                ) {
                    MMATextField(
                        value = searchText,
                        onValueChange = { mapViewModel.updateSearchText(it) },
                        placeholder = stringResource(id = R.string.search_place),
                        isHidden = false,
                        textStyle = Typography.bodySmall,
                        unfocusedColor = onMapButtonText,
                        focusedColor = ButtonBlack
                    )
                }
                if (searchResult.results.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 13.dp, end = 63.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(onMapButtonBackground.copy(alpha = 0.65f))
                            .padding(horizontal = 10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        for (place in searchResult.results) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(3.dp)
                                    .clickable {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        mapViewModel.resetSearchResult()
                                        mapViewModel.resetSearchText()
                                        coroutineScope.launch {
                                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(place.location), CENTER_MAP_DURATION_IN_MS)
                                        }
                                        mapViewModel.switchMarker(location = place.location, level = place.level)
                                        showMarkerInfo = true
                                    }
                            ) {
                                Text(text = place.name, style = Typography.bodyMedium)
                                if (!place.buildingName.isNullOrBlank())
                                    Text(text = "${place.buildingName}, ${stringResource(id = R.string.floor)} ${place.level}", style = Typography.labelMedium)
                            }
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(onMapButtonText))
                        }
                    }
                }
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapStyleOptions = MapStyleOptions(MapStyle.mapStyleJson),
                    latLngBoundsForCameraTarget = LatLngBounds(
                        LatLng(map.southWestBound!!.latitude, map.southWestBound.longitude),
                        LatLng(map.northEastBound!!.latitude, map.northEastBound.longitude)
                    ),
                    minZoomPreference = 17f,
                    maxZoomPreference = 19f,
                ), //app must have a localisation permission first in order to enable it
                onMyLocationButtonClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    mapViewModel.resetSearchResult()
                    mapViewModel.resetSearchText()
                    false
                                          },
                uiSettings = mapUiSettings,
                onMapClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    mapViewModel.resetSearchResult()
                    mapViewModel.resetSearchText()
                    if (showMarkerInfo) {
                        mapViewModel.resetMarker()
                        showMarkerInfo = false
                    }
                },
                onMapLongClick = {
                    mapViewModel.clearRoutes()
                    selectedLocation.value = it
                },
                contentPadding = PaddingValues(top = 60.dp),
                cameraPositionState = cameraPositionState,

            ) {
                if (departureLocation.value != null) {
                    drivingPath?.let {
                        val path = emptyArray<LatLng>()
                        Polyline(
                            points = (path + departureLocation.value!! + drivingPath!! + selectedLocation.value!!).toList(),
                            color = Color.White,
                            visible = driving,
                        )
                    }
                    walkingPath?.let {
                        val path = emptyArray<LatLng>()
                        Polyline(
                            points = (path + departureLocation.value!! + walkingPath!! + selectedLocation.value!!).toList(),
                            color = Color.White,
                            visible = !driving,
                        )
                    }
                }
                val markerParkingBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker_parking), 100, 100, true))
                val markerBuildingBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker_building), 100, 100, true))
                val markerOtherBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker_other), 100, 100, true))
                val biggerMarkerParkingBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker_parking), 120, 120, true))
                val biggerMarkerBuildingBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker_building), 120, 120, true))
                val biggerMarkerOtherBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.marker_other), 120, 120, true))
                val markerUnknownBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.unknown_location), 100, 100, true))
                if (map.markers != null) {
                    for (marker in map.markers) {
                        Marker(
                            state = MarkerState(marker.localisation),
                            icon = when (marker.type) {
                                MarkerType.BUILDING ->
                                    if (displayedMarker == marker) biggerMarkerBuildingBitmap else markerBuildingBitmap
                                MarkerType.PARKING ->
                                    if (displayedMarker == marker) biggerMarkerParkingBitmap else markerParkingBitmap
                                else ->
                                    if (displayedMarker == marker) biggerMarkerOtherBitmap else markerOtherBitmap
                            },
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                mapViewModel.resetSearchResult()
                                mapViewModel.resetSearchText()
                                coroutineScope.launch {
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLng(marker.localisation), CENTER_MAP_DURATION_IN_MS)
                                }
                                selectedLocation.value = marker.localisation
                                mapViewModel.clearRoutes()
                                mapViewModel.switchMarker(marker)
                                showMarkerInfo = true
                                true
                            }
                        )
                    }
                }
                selectedLocation.value?.let {
                    Marker(
                        title = "(${it.latitude}) (${it.longitude})",
                        state = MarkerState(position = it),
                        icon = markerUnknownBitmap,
                    )
                    mapViewModel.switchMarker(
                        MarkerData(
                            markerName = stringResource(id = R.string.place_on_the_map),
                            localisation = it,
                            markerDescription = null,
                            type = MarkerType.OTHER,
                        )
                    )
                    showMarkerInfo = true
                }
                departureLocation.value?.let {
                    Marker(
                        title = "out of bounds - (${it.latitude}) (${it.longitude})",
                        state = MarkerState(position = it),
                        icon = markerUnknownBitmap,
                    )
                }
            }
            if (showMarkerInfo) {
                Column {
                    Box(modifier = Modifier.weight(1.3f))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .shadow(elevation = 5.dp)
                                .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = displayedMarker.markerName
                                        ?: stringResource(id = R.string.not_found),
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    style = Typography.titleSmall,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row {
                                    if (routes != null) {
                                        Box(
                                            modifier = Modifier
                                                .shadow(5.dp, RoundedCornerShape(15.dp))
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(MaterialTheme.colorScheme.secondary)
                                                .clickable {
                                                    driving = false
                                                }
                                                .padding(5.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.directions_walk),
                                                contentDescription = stringResource(R.string.directions_walk),
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Box(
                                            modifier = Modifier
                                                .shadow(5.dp, RoundedCornerShape(15.dp))
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(MaterialTheme.colorScheme.secondary)
                                                .clickable {
                                                    driving = true
                                                }
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.directions_car),
                                                contentDescription = stringResource(R.string.directions_car),
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(5.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .shadow(5.dp, RoundedCornerShape(15.dp))
                                            .size(30.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(MaterialTheme.colorScheme.secondary)
                                            .clickable {
                                                if (hasLocationPermission) {
                                                    fusedLocationClient.lastLocation
                                                        .addOnSuccessListener { location: Location? ->
                                                            if (location != null) {
                                                                val latitude = when {
                                                                    location.latitude > map.northEastBound.latitude ->  map.northEastBound.latitude
                                                                    location.latitude < map.southWestBound.latitude -> { map.southWestBound.latitude }
                                                                    else -> location.latitude
                                                                }
                                                                val longitude = when {
                                                                    location.longitude > map.northEastBound.longitude ->  map.northEastBound.longitude
                                                                    location.longitude < map.southWestBound.longitude -> { map.southWestBound.longitude }
                                                                    else -> location.longitude
                                                                }
                                                                val destination = if (showMarkerInfo) displayedMarker.localisation else selectedLocation.value
                                                                departureLocation.value = LatLng(latitude, longitude)
                                                                Log.d("destination", destination.toString())
                                                                mapViewModel.getRoutes(departureLocation.value!!, destination!!, bundle.getString("com.google.android.geo.API_KEY"))
                                                            } else {
                                                                couldNotFindLocationToast.show()
                                                            }
                                                        }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.navigate_arrow),
                                            contentDescription = stringResource(
                                                id = R.string.navigate,
                                            ),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                                        )
                                    }
                                }
                            }
                            MMAContentBox {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (displayedMarker.markerDescription != "" && displayedMarker.markerDescription != null) {
                                        Text(
                                            text = displayedMarker.markerDescription as String,
                                            modifier = Modifier.padding(bottom = 10.dp),
                                            style = Typography.bodyMedium
                                        )
                                    }
                                    if (displayedMarker.type == MarkerType.BUILDING) {
                                        var selectedFloor by remember { mutableIntStateOf(mapViewModel.selectedLevel.value) }
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 10.dp)
                                        ) {
                                            for (floor in displayedMarker.floors) {
                                                Text(
                                                    "${stringResource(id = R.string.floor)} ${floor.level}",
                                                    style = Typography.bodyMedium,
                                                    modifier = Modifier.clickable {
                                                        selectedFloor = floor.level
                                                    }
                                                )
                                                if (selectedFloor == floor.level) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 10.dp)
                                                    ) {
                                                        if (floor.rooms.isNotEmpty()) {
                                                            for (room in floor.rooms) {
                                                                var showDescription by rememberSaveable { mutableStateOf(false) }
                                                                Column {
                                                                    Text(
                                                                        text = room.name,
                                                                        style = Typography.bodySmall,
                                                                        modifier = Modifier
                                                                            .clickable {
                                                                                showDescription = !(showDescription)
                                                                            }
                                                                    )
                                                                    if (showDescription) {
                                                                        Text(
                                                                            text = room.description,
                                                                            style = Typography.labelLarge,
                                                                            modifier = Modifier.padding(start = 5.dp)
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            Text(
                                                                stringResource(id = R.string.empty_floor),
                                                                style = Typography.bodySmall,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
