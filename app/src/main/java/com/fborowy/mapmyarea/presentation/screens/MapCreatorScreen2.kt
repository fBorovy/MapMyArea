package com.fborowy.mapmyarea.presentation.screens

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.MapCreatorViewModel
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

@Composable
fun MapCreatorScreen2(
    mapCreatorViewModel: MapCreatorViewModel,
    navController: NavController
) {
    var isInstructionPopupVisible by remember { mutableStateOf(false) }

    Log.d("MOJE", "${mapCreatorViewModel.getBoundaries()}")

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
                    contentDescription = stringResource(id = R.string.go_back),
                )
            }
            Text(
                text = stringResource(R.string.map_creator_screen_title),
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
            properties = MapProperties(
                isMyLocationEnabled = true, //app must have a localisation permission first in order to enable it
                latLngBoundsForCameraTarget = mapCreatorViewModel.getBoundaries(),
                minZoomPreference = 16f,
                maxZoomPreference = 20f
            ),
            //onMyLocationButtonClick = { false },
            uiSettings = mapUiSettings,
            onMapClick = {

            },
        )


    }
}


