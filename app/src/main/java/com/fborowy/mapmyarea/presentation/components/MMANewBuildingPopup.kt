package com.fborowy.mapmyarea.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.InstructionPopupGray
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun MMANewBuildingPopup(
    onDismiss: () -> Unit
) {

    val context = LocalContext.current
    val floorsList = remember { mutableStateListOf(0) }
    var isInstructionPopupVisible by remember { mutableStateOf(false) }
    val areRemoveFloorButtonsActive by remember { derivedStateOf{floorsList.size > 1} }
    val areAddFloorButtonsActive by remember { derivedStateOf { floorsList.size < 164 }}
    val scrollState = rememberScrollState()
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

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        if (isInstructionPopupVisible) {
            MMAInstructionPopup(content = stringResource(R.string.building_creator_instruction), onDismiss = { isInstructionPopupVisible = false })
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 110.dp)
                .clip(RoundedCornerShape(25.dp))
                .border(width = 2.dp, shape = RoundedCornerShape(25.dp), color = TextWhite)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, InstructionPopupGray),
                    )
                )
        ) {
            Text(
                stringResource(id = R.string.new_building_popup_title),
                style = Typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.TopCenter)
            )
            Box(
                modifier = Modifier
                    .padding(top = 15.dp, end = 10.dp)
                    .size(44.dp)
                    .border(1.dp, TextWhite, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .clickable {
                        isInstructionPopupVisible = true
                    }
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "?",
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                if (areRemoveFloorButtonsActive) {
                                    floorsList.removeAt(0)
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                }
                                else
                                    oneFloorToast.show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.remove_first_floor),
                            textAlign = TextAlign.Center,
                            style = Typography.bodyMedium,
                            color = if (areRemoveFloorButtonsActive) TextWhite else InstructionPopupGray
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            stringResource(id = R.string.add_lower_floor),
                            style = Typography.bodyMedium,
                            modifier = Modifier
                                .clickable {
                                    if (areAddFloorButtonsActive) {
                                        floorsList.add(0, floorsList.first() - 1)
                                        coroutineScope.launch {
                                            scrollState.animateScrollTo(0)
                                        }
                                    }
                                    else
                                        maxFloorAmountToast.show()
                                }
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .verticalScroll(scrollState)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                floorsList.forEach { level ->
                                    Text(
                                        text = stringResource(id = R.string.floor) + " " + level,
                                        style = Typography.bodyMedium,
                                        modifier = Modifier
                                            .clickable {

                                            }
                                    )
                                }
                            }
                        }
                        Text(
                            text = stringResource(id = R.string.add_higher_floor),
                            style = Typography.bodyMedium,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .clickable {
                                    if (areAddFloorButtonsActive) {
                                        floorsList.add(floorsList.last() + 1)
                                        coroutineScope.launch {
                                            scrollState.animateScrollTo(scrollState.maxValue)
                                        }
                                    }
                                    else
                                        maxFloorAmountToast.show()
                                }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable {
                                if (areRemoveFloorButtonsActive) {
                                    floorsList.removeLast()
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                                else
                                    oneFloorToast.show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.remove_last_floor),
                            textAlign = TextAlign.Center,
                            style = Typography.bodyMedium,
                            color = if (areRemoveFloorButtonsActive) TextWhite else InstructionPopupGray
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    MMAButton(
                        text = stringResource(id = R.string.cancel),
                        style = Typography.bodyMedium,
                        horizontalPadding = 17.dp,
                        onClick = {

                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    MMAButton(
                        text = stringResource(id = R.string.save),
                        style = Typography.bodyMedium,
                        horizontalPadding = 25.dp,
                        onClick = {

                        }
                    )
                }
            }

        }
    }
}