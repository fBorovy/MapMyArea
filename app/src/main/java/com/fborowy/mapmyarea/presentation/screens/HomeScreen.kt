package com.fborowy.mapmyarea.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.domain.view_models.AppViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    onSignOut: () -> Unit,
    onCreateMap: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val savedMaps = mutableListOf<MapData>()
    val ownedMaps = mutableListOf<MapData>()
    val searchText by appViewModel.searchText.collectAsState()
    val addingMapState by appViewModel.addingMapState.collectAsState()
    val userData by appViewModel.userData.collectAsState()
//    val collectingUserDataError by appViewModel.collectingUserInfoError.collectAsState()
//    var showSearchBarInstruction by rememberSaveable { mutableStateOf(false) }
//    var showSavedMapRemovalConfirmationDialog by rememberSaveable { mutableStateOf(false) }
//    var showOwnMapDeletionConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var mapToDeleteName by rememberSaveable { mutableStateOf("") }
    val mapDeletionIssue by appViewModel.mapDeletionIssue.collectAsState()

//    if (userData.userId == null || collectingUserDataError != "") {
//        Toast.makeText(
//            context, collectingUserDataError, Toast.LENGTH_LONG
//        ).show()
//        onSignOut()
//    }
    for (map in userData.savedMaps!!) {
        if (userData.userId == map.owner) ownedMaps.add(map)
        else savedMaps.add(map)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = userData?.username ?: context.getString(R.string.user),
                style = Typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Row() {
            MMAButton(
                text = context.getString(R.string.create_map),
                onClick = onCreateMap
            )
            Spacer(modifier = Modifier.width(10.dp))
            MMAButton(
                text = context.getString(R.string.signOut),
                onClick = onSignOut
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Text(
                text = context.getString(R.string.user_maps),
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}