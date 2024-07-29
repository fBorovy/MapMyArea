package com.fborowy.mapmyarea.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.view_models.ValidateCredentialsViewModel
import com.fborowy.mapmyarea.domain.email_auth.EmailAuthClient
import com.fborowy.mapmyarea.presentation.components.MMATextField
import com.fborowy.mapmyarea.ui.theme.ButtonBlack
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun EmailSignUpScreen(
    navController: NavController,
    emailAuthClient: EmailAuthClient,
    onSignUpClick: (SignInResult) -> Unit,
) {
    val context = LocalContext.current
    val validationViewModel = viewModel<ValidateCredentialsViewModel>()

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
                    .padding(start = 15.dp)
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
                value = validationViewModel.email,
                onValueChange = { validationViewModel.updateEmailField(it) },
                placeholder = { Text(context.resources.getString(R.string.enter_email)) },
                isHidden = false
            )
            Spacer(modifier = Modifier.height(30.dp))
            MMATextField(
                value = validationViewModel.password1,
                onValueChange = { validationViewModel.updatePassword1Field(it) },
                placeholder = { Text(context.resources.getString(R.string.enter_password)) },
                isHidden = true
            )
            Spacer(modifier = Modifier.height(15.dp))
            MMATextField(
                value = validationViewModel.password2,
                onValueChange = { validationViewModel.updatePassword2Field(it) },
                placeholder = { Text(context.resources.getString(R.string.confirm_password)) },
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
                        val result = validationViewModel.validate()
                        if (result != 0) showRegisteringErrorMessage(context, error = result)
                        else {
                            try {
                                emailAuthClient.signUpWithEmail(
                                    validationViewModel.email,
                                    validationViewModel.password2,
                                ) {
                                    onSignUpClick(it)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.failed_to_sign_in),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = context.resources.getString(R.string.register), style = Typography.titleMedium)
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