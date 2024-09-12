package com.fborowy.mapmyarea.presentation.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
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
import com.google.maps.android.compose.Polygon

@Composable
fun MapCreatorScreen1(
    mapCreatorViewModel: MapCreatorViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val boundsNotSetToast = Toast.makeText(
        context,
        context.getString(R.string.map_boundaries_not_set),
        Toast.LENGTH_LONG
    )
    val mapOversizeToast = Toast.makeText(
        context,
        context.getString(R.string.map_boundaries_not_within_limit),
        Toast.LENGTH_LONG
    )
    val corner1 by mapCreatorViewModel.corner1position.collectAsState()
    val corner2 by mapCreatorViewModel.corner2position.collectAsState()
    var isInstructionVisible by rememberSaveable { mutableStateOf(true) }

    val mapUiSettings = MapUiSettings(
        rotationGesturesEnabled = true,
        myLocationButtonEnabled = true, //isMyLocationEnabled has to be true too to display it
        compassEnabled = true,
        //mapToolbarEnabled = false,
    )

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
                        mapCreatorViewModel.resetNewMapState()
                        navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    stringResource(id = R.string.set_map_boundaries_instruction),
                    style = Typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                )
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapStyleOptions = MapStyleOptions(MapStyle.mapStyleJson),
                ), //app must have a localisation permission first in order to enable it
                //onMyLocationButtonClick = { false },
                uiSettings = mapUiSettings,
                onMapClick = { latLng ->
                    mapCreatorViewModel.setCorner(latLng)
                },
                onMapLongClick = {
                    mapCreatorViewModel.resetCorners()
                },
                contentPadding = PaddingValues(top = 20.dp)
            ) {
                corner1?.let {
                    Marker(
                        state = MarkerState(position = it),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    )
                }
                corner2?.let { corner2 ->
                    Marker(
                        state = MarkerState(position = corner2),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    )
                    Polygon(
                        points = listOf(
                            LatLng(corner1!!.latitude, corner1!!.longitude),
                            LatLng(corner2.latitude, corner1!!.longitude),
                            LatLng(corner2.latitude, corner2.longitude),
                            LatLng(corner1!!.latitude, corner2.longitude)
                        ),
                        fillColor = Color(0x330000FF),
                        strokeColor = Color.Blue
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            ) {
                MMAButton(
                    text = stringResource(R.string.proceed),
                    onClick = {
                        val result = mapCreatorViewModel.setBoundaries()
                        when (result) {
                            0 -> {
                                navController.navigate(Screen.MapCreatorScreen2.route)
                            }
                            1 -> boundsNotSetToast.show()
                            2 -> mapOversizeToast.show()
                        }
                    },
                    backgroundColor = onMapButtonBackground.copy(alpha = 0.75f),
                    textColor = onMapButtonText
                )
            }
        }
    }
    if (mapCreatorViewModel.isInstructionScreen1Visible && isInstructionVisible) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.set_map_boundaries_instruction),
            onDismiss = {
                mapCreatorViewModel.isInstructionScreen1Visible = false
                isInstructionVisible = false
            }
        )
    }
}

