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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.EmailAuthClient
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.view_models.RegistrationViewModel
import com.fborowy.mapmyarea.presentation.components.MMAButton
import com.fborowy.mapmyarea.presentation.components.MMAContentBox
import com.fborowy.mapmyarea.presentation.components.MMAHeader
import com.fborowy.mapmyarea.presentation.components.MMATextField

@Composable
fun EmailSignUpScreen(
    navController: NavController,
    emailAuthClient: EmailAuthClient,
    onSignUpClick: (SignInResult) -> Unit,
) {
    val context = LocalContext.current
    val registrationViewModel = viewModel<RegistrationViewModel>()
    val keyboardController = LocalSoftwareKeyboardController.current
    val maxUsernameLength = 50
    val maxPasswordLength = 30
    val errorCode by registrationViewModel.errorCode.collectAsState()

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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MMAHeader(
            header = stringResource(id = R.string.app_name),
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
                    value = registrationViewModel.email,
                    onValueChange = {
                        if (it.length <= maxUsernameLength) {
                            registrationViewModel.updateEmailField(it)
                        } else {
                            registrationViewModel.updateEmailField(it.take(maxUsernameLength))
                        }
                                    },
                    placeholder = stringResource(R.string.enter_email),
                    isHidden = false,
                )
                Spacer(modifier = Modifier.height(30.dp))
                MMATextField(
                    value = registrationViewModel.password1,
                    onValueChange = {
                        if (it.length <= maxPasswordLength) {
                            registrationViewModel.updatePassword1Field(it)
                        } else {
                            registrationViewModel.updatePassword1Field(it.take(maxPasswordLength))
                        }},
                    placeholder = stringResource(R.string.enter_password),
                    isHidden = true,
                )
                Spacer(modifier = Modifier.height(15.dp))
                MMATextField(
                    value = registrationViewModel.password2,
                    onValueChange = {
                        if (it.length <= maxPasswordLength) {
                            registrationViewModel.updatePassword2Field(it)
                        } else {
                            registrationViewModel.updatePassword2Field(it.take(maxPasswordLength))
                        }},
                    placeholder = stringResource(R.string.confirm_password),
                    isHidden = true,
                )
                Spacer(modifier = Modifier.height(50.dp))
                MMAButton(
                    text = stringResource(R.string.register),
                    onClick = {
                        registrationViewModel.validate()
                        if (errorCode == 0) {
                            registrationViewModel.signUp(emailAuthClient) {
                                if (it.data == null) {
                                    Toast.makeText(
                                        context,
                                        it.errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    onSignUpClick(it)
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    if (errorCode != 0) {
        Toast.makeText(
            context,
            stringResource(id = errorCode),
            Toast.LENGTH_LONG
        ).show()
        registrationViewModel.resetErrorCode()
    }
}
