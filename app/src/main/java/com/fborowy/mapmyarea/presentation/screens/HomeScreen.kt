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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.classes.UserData
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
    var userData by remember { mutableStateOf<UserData?>(null) }
    LaunchedEffect(key1 = Unit){
        userData = appViewModel.getSignedUserInfo()
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