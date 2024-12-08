package com.fborowy.mapmyarea.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.fborowy.mapmyarea.ui.theme.Typography
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MarkerConfigurationScreen(mapCreatorViewModel: MapCreatorViewModel, navController: NavController) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var markerName by rememberSaveable { mutableStateOf(mapCreatorViewModel.newMarkerState.value.markerName?: "") }
    var markerDescription by rememberSaveable { mutableStateOf(mapCreatorViewModel.newMarkerState.value.markerDescription?: "") }
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
    val errorCode by mapCreatorViewModel.markerConfigurationErrorCode.collectAsState()
    val maxMarkerNameLength = 40
    val maxMarkerDescriptionLength = 150

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            }
            .background(MaterialTheme.colorScheme.background)
            //.verticalScroll(rememberScrollState())
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MMAHeader(
            header = stringResource(
                id = R.string.new_point,
                stringResource(id = mapCreatorViewModel.getMarkerType().stringResource)
            ),
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
                    onValueChange = {
                        markerName =
                            if (it.length > maxMarkerNameLength) it.take(maxMarkerNameLength)
                            else it
                    },
                    placeholder = stringResource(id = R.string.enter_marker_name),
                    isHidden = false
                )
                Spacer(modifier = Modifier.height(10.dp))
                MMATextField(
                    value = markerDescription,
                    onValueChange = {
                        markerDescription = if (it.length > maxMarkerDescriptionLength) it.take(
                            maxMarkerDescriptionLength
                        )
                        else it
                    },
                    placeholder = stringResource(id = R.string.enter_marker_description),
                    isHidden = false
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (mapCreatorViewModel.getMarkerType() == MarkerType.BUILDING) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                MMAContentBox {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(floorsListScrollState)
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            MMAButton(
                                modifier = Modifier.offset(x = (-90).dp),
                                text = "+",
                                onClick = {
                                    if (areAddFloorButtonsActive) {
                                        mapCreatorViewModel.addFloor(onTop = false)
                                        coroutineScope.launch {
                                            floorsListScrollState.animateScrollTo(0)
                                        }
                                        mapCreatorViewModel.shiftSelectedFloor(floorsList[0].level - 1)
                                        navController.navigate(Screen.FloorConfigurationScreen.route)
                                    }
                                    else
                                        maxFloorAmountToast.show()
                                },
                                textColor = MaterialTheme.colorScheme.onTertiary,
                            )
                            MMAButton(
                                text = stringResource(id = R.string.floor) + " " + (floorsList[0].level - 1),
                                onClick = {
                                    if (areAddFloorButtonsActive) {
                                        mapCreatorViewModel.addFloor(onTop = false)
                                        coroutineScope.launch {
                                            floorsListScrollState.animateScrollTo(0)
                                        }
                                        mapCreatorViewModel.shiftSelectedFloor(floorsList[0].level)
                                        navController.navigate(Screen.FloorConfigurationScreen.route)
                                    }
                                    else
                                        maxFloorAmountToast.show()
                                },
                                textColor = MaterialTheme.colorScheme.onTertiary,
                            )
                        }
                        floorsList.forEachIndexed { index, floor ->
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index == 0) {
                                    MMAButton(
                                        modifier = Modifier.offset(x = (-90).dp),
                                        text = "×",
                                        iconResource = R.drawable.delete_24,
                                        onClick = {
                                            if (areRemoveFloorButtonsActive) {
                                                mapCreatorViewModel.removeFloor(onTop = false)
                                                coroutineScope.launch {
                                                    floorsListScrollState.animateScrollTo(0)
                                                }
                                            } else
                                                oneFloorToast.show()
                                        },
                                        textColor = if (areRemoveFloorButtonsActive) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary,
                                    )
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                MMAButton(
                                    text = stringResource(id = R.string.floor) + " " + floor.level,
                                    onClick = {
                                        mapCreatorViewModel.shiftSelectedFloor(floor.level)
                                        Log.d("LEVEL", "${floor.level}")
                                        navController.navigate(Screen.FloorConfigurationScreen.route)
                                    },
                                    textColor = MaterialTheme.colorScheme.onSecondary,
                                )
                                if (index == floorsList.lastIndex) {
                                    MMAButton(
                                        modifier = Modifier.offset(x = (-90).dp),
                                        text = "×",
                                        iconResource = R.drawable.delete_24,
                                        onClick = {
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
                                        textColor = if (areRemoveFloorButtonsActive) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            MMAButton(
                                modifier = Modifier.offset(x = (-90).dp),
                                text = "+",
                                onClick = {
                                    if (areAddFloorButtonsActive) {
                                        mapCreatorViewModel.addFloor(onTop = true)
                                        coroutineScope.launch {
                                            floorsListScrollState.animateScrollTo(floorsListScrollState.maxValue)
                                        }
                                        mapCreatorViewModel.shiftSelectedFloor(floorsList[floorsList.lastIndex].level + 1)
                                        navController.navigate(Screen.FloorConfigurationScreen.route)
                                    }
                                    else
                                        maxFloorAmountToast.show()
                                },
                                textColor = MaterialTheme.colorScheme.onTertiary,
                            )
                            MMAButton(
                                text = stringResource(id = R.string.floor) + " " + (floorsList[floorsList.lastIndex].level + 1),
                                onClick = {
                                    if (areAddFloorButtonsActive) {
                                        mapCreatorViewModel.addFloor(onTop = true)
                                        coroutineScope.launch {
                                            floorsListScrollState.animateScrollTo(floorsListScrollState.maxValue)
                                        }
                                        mapCreatorViewModel.shiftSelectedFloor(floorsList[floorsList.lastIndex].level + 1)
                                        navController.navigate(Screen.FloorConfigurationScreen.route)
                                    }
                                    else
                                        maxFloorAmountToast.show()
                                },
                                textColor = MaterialTheme.colorScheme.onTertiary,
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
                if (markerName.length > 2) {
                    mapCreatorViewModel.setMarkerNameDescription(markerName, markerDescription)
                    val succeed = mapCreatorViewModel.addEditMarker()
                    if (succeed) {
                        navController.popBackStack()
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.marker_name_shorter_than_3),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
        //Spacer(modifier = Modifier.height(20.dp))
    }
    if (errorCode != null) {
        Toast.makeText(
            context,
            stringResource(id = errorCode!!),
            Toast.LENGTH_LONG
        ).show()
        mapCreatorViewModel.resetMarkerErrorCode()
    }
}



//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .weight(1f),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                    ) {
//                        Text(
//                            stringResource(id = R.string.add_lower_floor),
//                            style = Typography.bodySmall,
//                            modifier = Modifier
//                                .clickable {
//                                    if (areAddFloorButtonsActive) {
//                                        mapCreatorViewModel.addFloor(onTop = false)
//                                        coroutineScope.launch {
//                                            floorsListScrollState.animateScrollTo(0)
//                                        }
//                                    }
//                                    else
//                                        maxFloorAmountToast.show()
//                                },
//                            color = MaterialTheme.colorScheme.onSecondary,
//                            textAlign = TextAlign.Center
//                        )
//                        Box(
//                            contentAlignment = Alignment.Center,
//                            modifier = Modifier
//                                .padding(top = 10.dp, bottom = 10.dp)
//                        ) {


//                        Text(
//                            text = stringResource(id = R.string.add_higher_floor),
//                            style = Typography.bodySmall,
//                            modifier = Modifier
//                                .clickable {
//                                    if (areAddFloorButtonsActive) {
//                                        mapCreatorViewModel.addFloor(onTop = true)
//                                        coroutineScope.launch {
//                                            floorsListScrollState.animateScrollTo(
//                                                floorsListScrollState.maxValue
//                                            )
//                                        }
//                                    } else
//                                        maxFloorAmountToast.show()
//                                },
//                            color = MaterialTheme.colorScheme.onSecondary,
//                            textAlign = TextAlign.Center
//                        )
//}
//                    Box(
//                        modifier = Modifier
//                            .padding(end = 1.dp)
//                            .clickable {
//                                if (areRemoveFloorButtonsActive) {
//                                    mapCreatorViewModel.removeFloor(onTop = true)
//                                    coroutineScope.launch {
//                                        floorsListScrollState.animateScrollTo(
//                                            floorsListScrollState.maxValue
//                                        )
//                                    }
//                                } else
//                                    oneFloorToast.show()
//                            },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = stringResource(id = R.string.remove_last_floor),
//                            style = Typography.bodySmall,
//                            textAlign = TextAlign.Center,
//                            color = if (areRemoveFloorButtonsActive) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
//                        )
//                    }