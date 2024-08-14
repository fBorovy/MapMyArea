package com.fborowy.mapmyarea.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.fborowy.mapmyarea.domain.email_auth.EmailAuthClient
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMATextField

@Composable
fun EmailSignInScreen(
    navController: NavController,
    emailAuthClient: EmailAuthClient,
    onSignInClick: (SignInResult) -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide() // Ukrywa klawiaturę
                })
            }
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
    ) {
        MMAHeader(
            header = context.resources.getString(R.string.app_name),
            onGoBack = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.height(25.dp))
        MMAContentBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MMATextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text(text = stringResource(R.string.enter_email)) },
                    isHidden = false,
                )
                Spacer(modifier = Modifier.height(15.dp))
                MMATextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text(text = stringResource(R.string.enter_password)) },
                    isHidden = true,
                )
                Spacer(modifier = Modifier.height(25.dp))
                MMAButton(
                    text = context.resources.getString(R.string.sign_in),
                    onClick = {
                        try {
                            emailAuthClient.signInWithEmail(
                                email,
                                password
                            ) { onSignInClick(it) }
                        } catch (e: Exception) {
                            Toast
                                .makeText(
                                    context,
                                    context.resources.getString(R.string.failed_to_sign_in),
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                    }
                )
            }
        }
    }
}