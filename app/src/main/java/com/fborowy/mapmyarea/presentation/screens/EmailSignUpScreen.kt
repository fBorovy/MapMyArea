package com.fborowy.mapmyarea.presentation.screens

import android.content.Context
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
import com.fborowy.mapmyarea.domain.email_auth.EmailAuthClient
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
                    onValueChange = { registrationViewModel.updateEmailField(it) },
                    placeholder = { Text( text = stringResource(R.string.enter_email)) },
                    isHidden = false,
                )
                Spacer(modifier = Modifier.height(30.dp))
                MMATextField(
                    value = registrationViewModel.password1,
                    onValueChange = { registrationViewModel.updatePassword1Field(it) },
                    placeholder = { Text(text = stringResource(R.string.enter_password)) },
                    isHidden = true,
                )
                Spacer(modifier = Modifier.height(15.dp))
                MMATextField(
                    value = registrationViewModel.password2,
                    onValueChange = { registrationViewModel.updatePassword2Field(it) },
                    placeholder = { Text( text = stringResource(R.string.confirm_password)) },
                    isHidden = true,
                )
                Spacer(modifier = Modifier.height(50.dp))
                MMAButton(
                    text = stringResource(R.string.register),
                    onClick = {
                        val result = registrationViewModel.validate()
                        if (result != 0) showRegisteringErrorMessage(context, error = result)
                        else {
                            try {
                                registrationViewModel.signUp(emailAuthClient){
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
                            } catch (e: Exception) {
                                Toast
                                    .makeText(
                                        context,
                                        if (e.message == "exists") context.resources.getString(R.string.user_already_exists)
                                        else context.resources.getString(R.string.failed_to_sign_in),
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                    }
                )
            }
        }
    }
}


fun showRegisteringErrorMessage(context: Context, error: Int) {

    when (error) {
        1 -> Toast.makeText(
            context,
            context.resources.getString(R.string.short_password_error),
            Toast.LENGTH_LONG
        ).show()
        2 -> Toast.makeText(
            context,
            context.resources.getString(R.string.passwords_differ_error),
            Toast.LENGTH_LONG
        ).show()
        3 -> Toast.makeText(
            context,
            context.resources.getString(R.string.empty_email_error),
            Toast.LENGTH_LONG
        ).show()
    }
}