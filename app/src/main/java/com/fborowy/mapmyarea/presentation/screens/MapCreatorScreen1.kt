package com.fborowy.mapmyarea.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.MapCreatorViewModel
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.presentation.components.MMAInstructionPopup
import com.fborowy.mapmyarea.ui.theme.ButtonBlack
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
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
    var isInstructionPopupVisible by remember { mutableStateOf(false) }

    var clickCount by remember { mutableIntStateOf(0) }
    var corner1position by remember { mutableStateOf<LatLng?>(null) }
    var corner2position by remember { mutableStateOf<LatLng?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, TextWhite),
                    )
                )
                .padding(vertical = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .padding(start = 15.dp)
                    .clickable { navController.popBackStack() },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_40),
                    contentDescription = stringResource(R.string.go_back),
                )
            }
            Text(
                stringResource(R.string.map_creator_screen_title),
                style = Typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 74.dp)
                    .align(Alignment.CenterStart)
            )
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterEnd)
                    .padding(end = 20.dp)
                    .size(44.dp)
                    .border(1.dp, TextWhite, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .clickable {
                        isInstructionPopupVisible = !isInstructionPopupVisible
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "?", style = Typography.titleLarge)
            }
        }

        val mapUiSettings = MapUiSettings(
            rotationGesturesEnabled = true,
            myLocationButtonEnabled = true, //isMyLocationEnabled has to be true too to display it
            compassEnabled = true,
            //mapToolbarEnabled = false,
        )

        GoogleMap(
            properties = MapProperties(isMyLocationEnabled = true), //app must have a localisation permission first in order to enable it
            //onMyLocationButtonClick = { false },
            uiSettings = mapUiSettings,
            onMapClick = { latLng ->
                if (clickCount == 0) {
                    clickCount = 1
                    corner1position = latLng
                }
                else if (clickCount == 1) {
                    clickCount = 2
                    corner2position = latLng
                }
            },
            onMapLongClick = {
                clickCount = 0
                corner1position = null
                corner2position = null
            }
        ) {
            corner1position?.let {
                Marker(
                    state = MarkerState(position = it),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
            corner2position?.let {
                Marker(
                    state = MarkerState(position = it),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)

                )
            }
            corner1position?.let { corner1 ->
                corner2position?.let { marker2 ->
                    Polygon(
                        points = listOf(
                            LatLng(corner1.latitude, corner1.longitude),
                            LatLng(marker2.latitude, corner1.longitude),
                            LatLng(marker2.latitude, marker2.longitude),
                            LatLng(corner1.latitude, marker2.longitude)
                        ),
                        fillColor = Color(0x330000FF), // Kolor wypełnienia
                        strokeColor = Color.Blue // Kolor obramowania
                    )
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(ButtonBlack)
                .padding(13.dp)
                .clickable {
                    if (clickCount == 2 && mapCreatorViewModel.isAreaWithinLimit(3.0, corner1position, corner2position)) {
                        mapCreatorViewModel.setBoundaries(corner1position, corner2position)
                        navController.navigate(Screen.MapCreatorScreen2.route)
                    } else {
                        clickCount = 0
                        corner1position = null
                        corner2position = null
                        Toast
                            .makeText(
                                context,
                                context.getString(R.string.map_boundaries_not_set),
                                Toast.LENGTH_LONG
                            )
                            .show()
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.proceed), style = Typography.bodyMedium)
        }
    }
    if (isInstructionPopupVisible) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.set_map_boundaries_instruction),
            onDismiss = { isInstructionPopupVisible = false })
    }
}

