package com.fborowy.mapmyarea

import android.app.Activity.RESULT_OK
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fborowy.mapmyarea.domain.AppViewModel
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.domain.email_auth.EmailAuthClient
import com.fborowy.mapmyarea.domain.google_auth.GoogleAuthClient
import com.fborowy.mapmyarea.presentation.screens.EmailSignInScreen
import com.fborowy.mapmyarea.presentation.screens.EmailSignUpScreen
import com.fborowy.mapmyarea.presentation.screens.HomeScreen
import com.fborowy.mapmyarea.presentation.screens.StartScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    applicationContext: Context,
    lifecycleScope: LifecycleCoroutineScope
) {
    val viewModel = viewModel<AppViewModel>()
    val signInState by viewModel.signInState.collectAsStateWithLifecycle()
    val emailAuthClient = EmailAuthClient(viewModel.auth, viewModel.database)
    val googleAuthUiClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            viewModel.auth
        )
    }
    val navController = rememberNavController()



    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(Screen.StartScreen.route) {
            LaunchedEffect(key1 = Unit) {
                if (viewModel.auth.currentUser != null)
                    navController.navigate(Screen.HomeScreen.route)
            }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        lifecycleScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignIn(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = signInState.signInSuccessful){
                if (signInState.signInSuccessful){
                    val user = viewModel.auth.currentUser
                    viewModel.database.collection("users")
                        .document(user!!.uid)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (!(document.exists())) {
                                    val newFirestoreUser = hashMapOf(
                                        "username" to user.displayName,
                                        "savedMaps" to null,
                                    )
                                    viewModel.database.collection("users")
                                        .document(user.uid)
                                        .set(newFirestoreUser)
                                }
                            }
                        }
                    Toast.makeText(
                        applicationContext,
                        applicationContext.resources.getString(R.string.toast_sign_in),
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.HomeScreen.route)
                    viewModel.resetSignInState()
                }
            }

            StartScreen(
                signInState = signInState,
                onSignInClick = {
                    lifecycleScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                navController = navController,
            )
        }

        composable(route = Screen.HomeScreen.route){
            HomeScreen(
                viewModel = viewModel
            ) {
                lifecycleScope.launch {
                    googleAuthUiClient.signOut()
                    Toast.makeText(
                        applicationContext,
                        applicationContext.resources.getString(R.string.toast_sign_out),
                        Toast.LENGTH_LONG
                    ).show()
                    navController.popBackStack(Screen.StartScreen.route, false)
                }
            }
        }

        composable(route = Screen.EmailSignUpScreen.route){
            LaunchedEffect(key1 = signInState.signInSuccessful){
                if (signInState.signInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.resources.getString(R.string.toast_sign_in),
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.HomeScreen.route)
                    viewModel.resetSignInState()
                }
            }

            EmailSignUpScreen(
                navController = navController,
                emailAuthClient,
                onSignUpClick = {
                    viewModel.onSignIn(it)
                }
            )
        }

        composable(route = Screen.EmailSignInScreen.route){
            LaunchedEffect(key1 = signInState.signInSuccessful){
                if (signInState.signInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.resources.getString(R.string.toast_sign_in),
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.HomeScreen.route)
                    viewModel.resetSignInState()
                }
            }

            EmailSignInScreen(navController = navController, emailAuthClient, onSignInClick = {
                viewModel.onSignIn(it)
            })
        }
    }
}