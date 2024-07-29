package com.fborowy.mapmyarea.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.states.SignInState
import com.fborowy.mapmyarea.ui.theme.ButtonBlack
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun StartScreen(
    signInState: SignInState,
    onSignInClick: () -> Unit,
    navController: NavController,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = signInState.signInError) {
        signInState.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

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
            Text(context.resources.getString(R.string.app_name), style = Typography.titleLarge)
            Text(
                text = stringResource(id = R.string.credits),
                style = Typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 15.dp)
                    .clickable {
                        navController.navigate(Screen.CreditsScreen.route)
                    }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
                .padding(horizontal = 50.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(ButtonBlack)
                .padding(13.dp)
                .clickable { onSignInClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(context.resources.getString(R.string.login_with_google), style = Typography.titleMedium)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .padding(horizontal = 50.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(ButtonBlack)
                .padding(13.dp)
                .clickable { navController.navigate(Screen.EmailSignInScreen.route) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = context.resources.getString(R.string.login_with_email), style = Typography.titleMedium)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 260.dp)
                .padding(horizontal = 50.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(ButtonBlack)
                .padding(13.dp)
                .clickable { navController.navigate(Screen.EmailSignUpScreen.route) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = context.resources.getString(R.string.register), style = Typography.titleMedium)
        }
    }
}