package com.fborowy.mapmyarea.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMAInstructionPopup
import com.fborowy.mapmyarea.presentation.components.MMATextField
import kotlinx.coroutines.flow.map

@Composable
fun MapCreatorScreen3(
    mapCreatorViewModel: MapCreatorViewModel,
    navController: NavController,
    onMapCreated: (MapData) -> Unit,
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val mapName by mapCreatorViewModel.newMapState.map { it.name }.collectAsState(initial = "")
    val mapDescription by mapCreatorViewModel.newMapState.map { it.description }.collectAsState(initial = "")
    val savingMapState by mapCreatorViewModel.savingMapState.collectAsState()


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
            header = mapCreatorViewModel.originalMapName?: stringResource(id = R.string.map_creator_screen_title),
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
                    placeholder = stringResource(id = R.string.map_name),
                    isHidden = false,
                    enabled = mapCreatorViewModel.originalMapName == null,
                )
                Spacer(modifier = Modifier.height(10.dp))
                MMATextField(
                    value = mapDescription,
                    onValueChange = { mapCreatorViewModel.setNewMapDescription(it) },
                    placeholder = stringResource(id = R.string.map_description),
                    isHidden = false,
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        MMAButton(
            text = stringResource(id = R.string.save),
            onClick = {
                if (mapCreatorViewModel.checkFirestoreNameValidity()) {
                    try {
                        val map = mapCreatorViewModel.getMapDataFromMapState()
                        mapCreatorViewModel.saveMap(map)
                        onMapCreated(map)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(mapCreatorViewModel.errorMessage),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    if (savingMapState.saveMapSuccessful) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.map_saved_popup),
            onDismiss = {
                mapCreatorViewModel.resetSavingMapState()
                mapCreatorViewModel.resetNewMapState()
                navController.popBackStack(route = Screen.HomeScreen.route, inclusive = false) },
        )
    }
    if (savingMapState.saveMapError != null) {
        Toast.makeText(
            context,
            if (savingMapState.saveMapError == "exists") context.resources.getString(R.string.map_with_entered_name_already_exists)
                else savingMapState.saveMapError,
            Toast.LENGTH_LONG
        ).show()
        Log.d("ERROR", "${savingMapState.saveMapError}")
    }
}