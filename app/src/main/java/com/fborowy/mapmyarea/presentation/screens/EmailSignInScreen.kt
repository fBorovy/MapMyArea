package com.fborowy.mapmyarea.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.SignInResult
import com.fborowy.mapmyarea.domain.email_auth.EmailAuthClient
import com.fborowy.mapmyarea.presentation.components.MMATextField
import com.fborowy.mapmyarea.ui.theme.ButtonBlack
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun EmailSignInScreen(
    navController: NavController,
    emailAuthClient: EmailAuthClient,
    onSignInClick: (SignInResult) -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .clickable { navController.popBackStack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_40),
                    contentDescription = context.getString(R.string.go_back)
                )
            }
            Text(context.resources.getString(R.string.app_name), style = Typography.titleLarge)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 50.dp, vertical = 60.dp)
        ) {
            MMATextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = context.resources.getString(R.string.enter_email_or_username)) },
                isHidden = false
            )
            Spacer(modifier = Modifier.height(15.dp))
            MMATextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(context.resources.getString(R.string.enter_password)) },
                isHidden = true
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 80.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(ButtonBlack)
                    .padding(13.dp)
                    .clickable {
                        emailAuthClient.signInWithEmail(
                            email,
                            password
                        ) { onSignInClick(it) }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = context.resources.getString(R.string.sign_in),
                    style = Typography.titleMedium
                )
            }
        }
    }
}