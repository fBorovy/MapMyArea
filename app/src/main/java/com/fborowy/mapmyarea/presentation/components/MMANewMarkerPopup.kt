package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.MarkerType
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.ui.theme.InstructionPopupGray
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMANewMarkerPopup(mapCreatorViewModel: MapCreatorViewModel, onDismiss: () -> Unit) {

    var selectedMarkerType by remember { mutableStateOf(MarkerType.Building) }
    var markerName by remember { mutableStateOf("") }
    var markerDescription by remember { mutableStateOf("") }



    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 120.dp)
                .clip(RoundedCornerShape(25.dp))
                .border(width = 2.dp, shape = RoundedCornerShape(25.dp), color = TextWhite)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, InstructionPopupGray),
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(id = R.string.new_marker_popup_title),
                    style = Typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                )
                MMATextField(
                    value = markerName,
                    onValueChange = { markerName = it },
                    placeholder = { Text(stringResource(id = R.string.enter_marker_name)) },
                    isHidden = false,
                    focusedColor = TextWhite,
                    unfocusedColor = InstructionPopupGray,
                )
                Spacer(modifier = Modifier.height(10.dp))
                MMATextField(
                    value = markerDescription,
                    onValueChange = { markerDescription = it },
                    placeholder = { Text(stringResource(id = R.string.enter_marker_description)) },
                    isHidden = false,
                    focusedColor = TextWhite,
                    unfocusedColor = InstructionPopupGray,
                )
                Text(
                    stringResource(id = R.string.chose_new_marker_type_popup_title),
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 25.dp, bottom = 10.dp)
                )
                MarkerType.entries.forEach { markerType ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .border(
                                1.dp,
                                if (selectedMarkerType == markerType) TextWhite else Color.Gray,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                            .clickable {
                                selectedMarkerType = markerType
                            }

                    ) {
                        Row{
                            Icon(
                                painter = painterResource(id = markerType.painterResource),
                                contentDescription = stringResource(id = markerType.stringResource),
                                tint = if (selectedMarkerType == markerType) TextWhite else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(id = markerType.stringResource),
                                style = Typography.bodySmall,
                                color = if (selectedMarkerType == markerType) TextWhite else Color.Gray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .border(1.dp, TextWhite, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .padding(horizontal = 6.dp)
                            .clickable {
                                onDismiss()
                            }
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = Typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .border(1.dp, TextWhite, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .clickable {
                                mapCreatorViewModel.setMarkerNameDescriptionType(
                                    markerName,
                                    markerDescription,
                                    selectedMarkerType
                                )
                                onDismiss()
                            }

                    ) {
                        Text(
                            text = stringResource(id = R.string.confirm),
                            style = Typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}