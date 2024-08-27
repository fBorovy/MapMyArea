package com.fborowy.mapmyarea.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.FloorData
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMATextField
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MarkerConfigurationScreen(mapCreatorViewModel: MapCreatorViewModel, navController: NavController) {

    val keyboardController = LocalSoftwareKeyboardController.current
    var markerName by rememberSaveable { mutableStateOf("") }
    var markerDescription by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val maxFloorAmount = 163
    val minFloorAmount = 1
    val floorsList by mapCreatorViewModel.newMarkerState.map { it.floors }.collectAsState(initial = listOf(
        FloorData(level = 0, rooms = emptyList())
    ))
    val areRemoveFloorButtonsActive by remember { derivedStateOf{floorsList.size > minFloorAmount } }
    val areAddFloorButtonsActive by remember { derivedStateOf { floorsList.size <= maxFloorAmount } }
    val coroutineScope = rememberCoroutineScope()
    val oneFloorToast = Toast.makeText(
        context,
        stringResource(id = R.string.one_floor_required),
        Toast.LENGTH_LONG
    )
    val maxFloorAmountToast = Toast.makeText(
        context,
        stringResource(id = R.string.max_floor_amount_easter_egg),
        Toast.LENGTH_LONG
    )
    val floorsListScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide() // Ukrywa klawiaturę
                })
            }
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MMAHeader(
            header = stringResource(id = R.string.new_point, stringResource(id = mapCreatorViewModel.getMarkerType().stringResource)),
            onGoBack = {
                mapCreatorViewModel.resetNewMarkerState()
                navController.popBackStack()
            }
        )
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MMATextField(
                    value = markerName,
                    onValueChange = { markerName = it },
                    placeholder = { Text(text = stringResource(id = R.string.enter_marker_name)) },
                    isHidden = false
                )
                Spacer(modifier = Modifier.height(10.dp))
                MMATextField(
                    value = markerDescription,
                    onValueChange = { markerDescription = it },
                    placeholder = { Text(text = stringResource(id = R.string.enter_marker_description)) },
                    isHidden = false
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (mapCreatorViewModel.getMarkerType() == MarkerType.Building) {
            MMAContentBox {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .clickable {
                                    if (areRemoveFloorButtonsActive) {
                                        mapCreatorViewModel.removeFloor(onTop = false)
                                        coroutineScope.launch {
                                            floorsListScrollState.animateScrollTo(0)
                                        }
                                    } else
                                        oneFloorToast.show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.remove_first_floor),
                                textAlign = TextAlign.Center,
                                color = if (areRemoveFloorButtonsActive) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                stringResource(id = R.string.add_lower_floor),
                                modifier = Modifier
                                    .clickable {
                                        if (areAddFloorButtonsActive) {
                                            mapCreatorViewModel.addFloor(onTop = false)
                                            coroutineScope.launch {
                                                floorsListScrollState.animateScrollTo(0)
                                            }
                                        }
                                        else
                                            maxFloorAmountToast.show()
                                    },
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Center
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .verticalScroll(floorsListScrollState)
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    floorsList.forEach { floor ->
                                        Text(
                                            text = stringResource(id = R.string.floor) + " " + floor.level,
                                            modifier = Modifier
                                                .clickable {
                                                    mapCreatorViewModel.shiftSelectedFloor(floor.level)
                                                    Log.d("LEVEL", "${floor.level}")
                                                    navController.navigate(Screen.FloorConfigurationScreen.route)
                                                },
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                            Text(
                                text = stringResource(id = R.string.add_higher_floor),
                                modifier = Modifier
                                    .clickable {
                                        if (areAddFloorButtonsActive) {
                                            mapCreatorViewModel.addFloor(onTop = true)
                                            coroutineScope.launch {
                                                floorsListScrollState.animateScrollTo(
                                                    floorsListScrollState.maxValue
                                                )
                                            }
                                        } else
                                            maxFloorAmountToast.show()
                                    },
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clickable {
                                    if (areRemoveFloorButtonsActive) {
                                        mapCreatorViewModel.removeFloor(onTop = true)
                                        coroutineScope.launch {
                                            floorsListScrollState.animateScrollTo(
                                                floorsListScrollState.maxValue
                                            )
                                        }
                                    } else
                                        oneFloorToast.show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.remove_last_floor),
                                textAlign = TextAlign.Center,
                                color = if (areRemoveFloorButtonsActive) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        MMAButton(
            text = stringResource(id = R.string.save),
            onClick = {
                mapCreatorViewModel.setMarkerNameDescription(markerName, markerDescription)
                mapCreatorViewModel.addNewMarkerToMap()
                mapCreatorViewModel.resetNewMarkerState()
                navController.popBackStack()
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}