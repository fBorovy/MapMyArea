package com.fborowy.mapmyarea.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.domain.view_models.AppViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAInstructionPopup
import com.fborowy.mapmyarea.presentation.components.MMATextField
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    onSignOut: () -> Unit,
    onCreateMap: () -> Unit,
    onOpenMap: (MapData) -> Unit,
    onEditMap: (MapData) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val searchText by appViewModel.searchText.collectAsState()
    val addingMapState by appViewModel.addingMapState.collectAsState()
    val userData by appViewModel.userData.collectAsState()
    val collectingUserDataError by appViewModel.collectingUserInfoError.collectAsState()
    var showSearchBarInstruction by rememberSaveable { mutableStateOf(false) }
    var showSavedMapRemovalConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var showOwnMapDeletionConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var mapToDeleteName by rememberSaveable { mutableStateOf("") }
    val mapDeletionIssue by appViewModel.mapDeletionIssue.collectAsState()
    val savedMaps: MutableList<MapData> = mutableListOf()
    val ownedMaps: MutableList<MapData> = mutableListOf()

    if (collectingUserDataError != "") {
        Toast.makeText(
            context,
            stringResource(id = R.string.error_getting_user_info),
            Toast.LENGTH_LONG
        ).show()
        onSignOut()
    }

    for (map in userData.savedMaps!!) {
        if (map.owner == userData.userId) ownedMaps.add(map)
        else savedMaps.add(map)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide() // Ukrywa klawiaturę
                    focusManager.clearFocus()
                })
            }
            .background(MaterialTheme.colorScheme.background)
            .padding(25.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = userData.username ?: context.getString(R.string.user),
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Row {
            MMAButton(
                text = context.getString(R.string.create_map),
                onClick = {
                    appViewModel.clearSearchText()
                    onCreateMap()
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            MMAButton(
                text = context.getString(R.string.signOut),
                onClick = {
                    appViewModel.clearSearchText()
                    onSignOut()
                }
            )
        }
        if (ownedMaps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(25.dp))
            MMAContentBox {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = context.getString(R.string.own_maps),
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    for (map in ownedMaps) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${map.mapName}",
                                style = Typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    appViewModel.clearSearchText()
                                    onOpenMap(map)
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(id = R.drawable.edit_pencil),
                                contentDescription = stringResource(id = R.string.edit_map_icon),
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(top = 4.dp)
                                    .clickable {
                                        appViewModel.clearSearchText()
                                        onEditMap(map)
                                    }
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.delete_24),
                                contentDescription = stringResource(id = R.string.delete_description),
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(top = 4.dp)
                                    .clickable {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        mapToDeleteName = map.mapName!!
                                        showOwnMapDeletionConfirmationDialog = true
                                    }
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                MMATextField(
                    value = searchText,
                    onValueChange = { appViewModel.updateSearchMapText(it) },
                    placeholder = stringResource(id = R.string.search_map),
                    isHidden = false
                )
                Spacer(modifier = Modifier.height(7.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MMAButton(
                        text = "?",
                        onClick = {
                            showSearchBarInstruction = true
                        }
                    )
                    MMAButton(
                        text = stringResource(id = R.string.add_map),
                        onClick = {
                            appViewModel.addMapToUserSavedMapsFromSearch()
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = context.getString(R.string.user_maps),
                    style = Typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.height(5.dp))
                for (map in savedMaps) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${map.mapName}",
                            style = Typography.bodyMedium,
                            modifier = Modifier.clickable {
                                appViewModel.clearSearchText()
                                onOpenMap(map)
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.delete_24),
                            contentDescription = stringResource(id = R.string.delete_description),
                            modifier = Modifier
                                .size(28.dp)
                                .padding(top = 4.dp)
                                .clickable {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    mapToDeleteName = map.mapName!!
                                    showSavedMapRemovalConfirmationDialog = true
                                }
                        )
                    }
                }
            }
        }
    }
    if (addingMapState.addingSuccessful) {
        appViewModel.clearSearchText()
        keyboardController?.hide()
        focusManager.clearFocus()
        appViewModel.clearSearchText()
        appViewModel.resetAddingMapState()
    }
    if (addingMapState.errorCode != null) {
        MMAInstructionPopup(
            content = stringResource(id = addingMapState.errorCode as Int),
            onDismiss = { appViewModel.resetAddingMapState() }
        )
    }
    if (showSearchBarInstruction) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.search_map_instruction),
            onDismiss = { showSearchBarInstruction = false }
        )
    }
    if (showSavedMapRemovalConfirmationDialog) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.assure_saved_map_removal),
            onDismiss = { showSavedMapRemovalConfirmationDialog = false },
            onDismissLabel = stringResource(id = R.string.cancel),
            onConfirm = {
                appViewModel.removeMapFromUserSaved(mapToDeleteName)
                mapToDeleteName = ""
                showSavedMapRemovalConfirmationDialog = false
            },
            onConfirmLabel = stringResource(id = R.string.confirm)
        )
    }
    if (showOwnMapDeletionConfirmationDialog) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.assure_own_map_deletion),
            onDismiss = { showOwnMapDeletionConfirmationDialog = false },
            onDismissLabel = stringResource(id = R.string.cancel),
            onConfirm = {
                appViewModel.deleteMap(mapToDeleteName)
                mapToDeleteName = ""
                showOwnMapDeletionConfirmationDialog = false
            }
        )
    }
    if (mapDeletionIssue) {
        MMAInstructionPopup(
            content = stringResource(id = R.string.map_not_deleted),
            onDismiss = { appViewModel.resetMapDeletionStatus() }
        )
    }
}