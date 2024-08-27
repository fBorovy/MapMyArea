package com.fborowy.mapmyarea.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.presentation.MapStyle
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMAInstructionPopup
import com.fborowy.mapmyarea.ui.theme.Typography
import com.fborowy.mapmyarea.ui.theme.onMapButtonBackground
import com.fborowy.mapmyarea.ui.theme.onMapButtonText
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MapCreatorScreen2(
    mapCreatorViewModel: MapCreatorViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    var isNewPointButtonVisible by remember { mutableStateOf(false) }
    var newMarkerPosition by remember { mutableStateOf<LatLng?>(null) }
    val markerAddBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.marker_add), 100, 100, true))
    val markerParkingBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.marker_parking), 100, 100, true))
    val markerBuildingBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.marker_building), 100, 100, true))
    val markerOtherBitmap = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.marker_other), 100, 100, true))
    var isInstructionVisible by rememberSaveable { mutableStateOf(true) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box {
            Column(
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
                    header = stringResource(id = R.string.map_creator_screen_title),
                    onGoBack = {
                        navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    stringResource(id = R.string.set_map_points_instruction),
                    style = Typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                )
            }
            val mapUiSettings = MapUiSettings(
                rotationGesturesEnabled = true,
                myLocationButtonEnabled = true, //isMyLocationEnabled has to be true too to display it
                compassEnabled = true,
                //mapToolbarEnabled = false,
            )

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                properties = MapProperties(
                    isMyLocationEnabled = true, //app must have a localisation permission first in order to enable it
                    mapStyleOptions = MapStyleOptions(MapStyle.mapStyleJson),
                    latLngBoundsForCameraTarget = mapCreatorViewModel.getBoundaries(),
                    minZoomPreference = 16f,
                    maxZoomPreference = 20f,
                ),
                //onMyLocationButtonClick = { false },
                uiSettings = mapUiSettings,
                onMapClick = { position ->
                    mapCreatorViewModel.setNewMarkerCoordinates(position)
                    newMarkerPosition = position
                    isNewPointButtonVisible = true
                },
                contentPadding = PaddingValues(top = 25.dp)
            ) {
                newMarkerPosition?.let {
                    Marker(
                        state = MarkerState(position = it),
                        icon = markerAddBitmap,
                        title = "(${it.latitude}, ${it.longitude})",
                        onInfoWindowClick = {
                            Toast.makeText(context, context.getString(R.string.cancel), Toast.LENGTH_LONG).show()
                        }
                    )
                }
                for (marker in mapCreatorViewModel.newMapState.value.markers) {
                    Marker(
                        state = MarkerState(position = marker.localisation),
                        icon = when (marker.type) {
                            MarkerType.BUILDING -> markerBuildingBitmap
                            MarkerType.PARKING -> markerParkingBitmap
                            else -> markerOtherBitmap
                        },
                        title = marker.markerName
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isNewPointButtonVisible) {
                    MMAButton(text = stringResource(id = R.string.new_building_button_text), onClick = {
                        mapCreatorViewModel.setMarkerType(MarkerType.BUILDING)
                        navController.navigate(Screen.MarkerConfigurationScreen.route) },
                        backgroundColor = onMapButtonBackground,
                        textColor = onMapButtonText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MMAButton(text = stringResource(id = R.string.new_parking_button_text), onClick = {
                        mapCreatorViewModel.setMarkerType(MarkerType.PARKING)
                        navController.navigate(Screen.MarkerConfigurationScreen.route) },
                        backgroundColor = onMapButtonBackground,
                        textColor = onMapButtonText)
                    Spacer(modifier = Modifier.height(10.dp))
                    MMAButton(text = stringResource(id = R.string.new_other_button_text), onClick = {
                        mapCreatorViewModel.setMarkerType(MarkerType.OTHER)
                        navController.navigate(Screen.MarkerConfigurationScreen.route) },
                        backgroundColor = onMapButtonBackground,
                        textColor = onMapButtonText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                MMAButton(
                    text = stringResource(R.string.proceed),
                    onClick = {
                        navController.navigate(Screen.MapCreatorScreen3.route)
                    },
                    backgroundColor = onMapButtonBackground,
                    textColor = onMapButtonText
                )
            }
        }
        if (mapCreatorViewModel.isInstructionScreen2Visible && isInstructionVisible) {
            MMAInstructionPopup(
                content = stringResource(id = R.string.set_map_points_instruction_popup),
                onDismiss = {
                    mapCreatorViewModel.isInstructionScreen2Visible = false
                    isInstructionVisible = false
                }
            )
        }
    }
}