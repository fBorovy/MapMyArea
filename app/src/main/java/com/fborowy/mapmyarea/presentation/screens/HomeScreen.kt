package com.fborowy.mapmyarea.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.UserData
import com.fborowy.mapmyarea.domain.AppViewModel
import com.fborowy.mapmyarea.ui.theme.DropDownMenuGray
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onSignOut: () -> Unit,
) {
    val context = LocalContext.current
    var userData by remember { mutableStateOf<UserData?>(null) }
    LaunchedEffect(key1 = Unit){
        userData = viewModel.getSignedUserInfo()
    }
    var isMenuVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, TextWhite),
                    )
                )
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = userData?.username ?: context.getString(R.string.user),
                        style = Typography.titleMedium
                    )
                    Box(
                        modifier = Modifier.clickable {
                            isMenuVisible = !isMenuVisible
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_menu_40),
                            contentDescription = context.getString(R.string.profile_menu),
                        )
                        DropdownMenu(
                            expanded = isMenuVisible,
                            onDismissRequest = { isMenuVisible = false },
                            modifier = Modifier
                                .background(DropDownMenuGray)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        context.getString(R.string.signOut),
                                        style = Typography.bodySmall
                                    )
                                       },
                                onClick = { onSignOut() },
                                modifier = Modifier
                                    .background(DropDownMenuGray)
                            )
                        }
                    }
                }
                Text(
                    text = context.getString(R.string.user_maps),
                    style = Typography.bodyMedium
                )
            }
        }
    }
}