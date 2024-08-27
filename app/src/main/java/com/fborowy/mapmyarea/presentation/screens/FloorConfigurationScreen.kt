package com.fborowy.mapmyarea.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.RoomData
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMATextField

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FloorConfigurationScreen(
    navController: NavController,
    mapCreatorViewModel: MapCreatorViewModel,
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val floor by mapCreatorViewModel.newFloorState.collectAsState()
    val rooms = floor.rooms
    var roomName by rememberSaveable { mutableStateOf("") }
    var roomDescription by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MMAHeader(
            header = stringResource(id  = R.string.floor) + " " + mapCreatorViewModel.newFloorState.value.level,
            onGoBack = {
                navController.popBackStack()
            }
        )
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MMATextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    placeholder = { Text(text = stringResource(id = R.string.enter_room_name)) },
                    isHidden = false
                )
                Spacer(modifier = Modifier.height(20.dp))
                MMATextField(
                    value = roomDescription,
                    onValueChange = { roomDescription = it },
                    placeholder = { Text(text = stringResource(id = R.string.enter_room_description)) },
                    isHidden = false
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        MMAButton(
            text = stringResource(id = R.string.add_room),
            onClick = {
                mapCreatorViewModel.addRoomToFloor(RoomData(name = roomName, description = roomDescription))
                roomName = ""
                roomDescription = ""
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(id = R.string.rooms))
        Spacer(modifier = Modifier.height(12.dp))
        for (room in rooms) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 2.dp)
            ) {
                Text(
                    text = room.name,
                    modifier = Modifier
                        .clickable {
                            roomName = room.name
                            roomDescription = room.description
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { mapCreatorViewModel.removeRoomFromFloor(room) },
                    painter = painterResource(id = R.drawable.delete_24),
                    contentDescription = stringResource(R.string.delete_description),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        MMAButton(
            text = stringResource(id = R.string.save),
            onClick = {
                navController.popBackStack()
            }
        )
    }
}