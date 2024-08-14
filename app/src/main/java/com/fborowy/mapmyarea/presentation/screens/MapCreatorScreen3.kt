package com.fborowy.mapmyarea.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMAInstructionPopup
import com.fborowy.mapmyarea.presentation.components.MMATextField
import kotlinx.coroutines.flow.map

@Composable
fun MapCreatorScreen3(mapCreatorViewModel: MapCreatorViewModel, navController: NavController) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSavedMapPopupVisible by remember { mutableStateOf(false) }
    val mapName by mapCreatorViewModel.newMapState.map { it.name }.collectAsState(initial = "")
    val mapDescription by mapCreatorViewModel.newMapState.map { it.description }.collectAsState(initial = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MMAHeader(
            header = stringResource(id = R.string.map_creator_screen_title),
            onGoBack = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.height(20.dp))
        MMAContentBox {
            Column(
                modifier = Modifier.padding(40.dp)
            ) {
                MMATextField(
                    value = mapName,
                    onValueChange = { mapCreatorViewModel.setNewMapName(it) },
                    placeholder = { Text(text = stringResource(id = R.string.map_name)) },
                    isHidden = false,
                )
                Spacer(modifier = Modifier.height(10.dp))
                MMATextField(
                    value = mapDescription,
                    onValueChange = { mapCreatorViewModel.setNewMapDescription(it) },
                    placeholder = { Text(text = stringResource(id = R.string.map_description)) },
                    isHidden = false,
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        MMAButton(
            text = stringResource(id = R.string.save),
            onClick = {
                try {
                    mapCreatorViewModel.saveNewMap()
                    mapCreatorViewModel.resetNewMapState()
                } catch (e: Exception) {
                    Toast.makeText(context, "NIE MA MAPY", Toast.LENGTH_LONG).show()
                }
                isSavedMapPopupVisible = true
            }
        )
    }
    if (isSavedMapPopupVisible) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.map_saved_popup),
            onDismiss = { navController.popBackStack(route = Screen.HomeScreen.route, inclusive = false) },
        )
    }
}