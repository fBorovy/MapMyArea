package com.fborowy.mapmyarea.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.states.SignInState
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(context.resources.getString(R.string.app_name), style = Typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MMAButton(text = context.resources.getString(
                    R.string.sign_in
                ),
                    onClick = { navController.navigate(Screen.EmailSignInScreen.route) }
                )
                Spacer(modifier = Modifier.height(25.dp))
                MMAButton(
                    context.resources.getString(R.string.login_with_google),
                    onClick = { onSignInClick() }
                )
                Spacer(modifier = Modifier.height(25.dp))
                MMAButton(
                    context.resources.getString(R.string.register),
                    onClick = { navController.navigate(Screen.EmailSignUpScreen.route) }
                )
            }
        }
    }
}