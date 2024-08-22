package com.fborowy.mapmyarea

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fborowy.mapmyarea.domain.Screen
import com.fborowy.mapmyarea.data.EmailAuthClient
import com.fborowy.mapmyarea.data.GoogleAuthClient
import com.fborowy.mapmyarea.domain.view_models.AppViewModel
import com.fborowy.mapmyarea.domain.view_models.LocationPermissionViewModel
import com.fborowy.mapmyarea.domain.view_models.MapCreatorViewModel
import com.fborowy.mapmyarea.domain.view_models.MapViewModel
import com.fborowy.mapmyarea.presentation.components.LocationPermissionDialog
import com.fborowy.mapmyarea.presentation.screens.CreditsScreen
import com.fborowy.mapmyarea.presentation.screens.EmailSignInScreen
import com.fborowy.mapmyarea.presentation.screens.EmailSignUpScreen
import com.fborowy.mapmyarea.presentation.screens.FloorConfigurationScreen
import com.fborowy.mapmyarea.presentation.screens.HomeScreen
import com.fborowy.mapmyarea.presentation.screens.MapCreatorScreen1
import com.fborowy.mapmyarea.presentation.screens.MapCreatorScreen2
import com.fborowy.mapmyarea.presentation.screens.MapCreatorScreen3
import com.fborowy.mapmyarea.presentation.screens.MapScreen
import com.fborowy.mapmyarea.presentation.screens.MarkerConfigurationScreen
import com.fborowy.mapmyarea.presentation.screens.StartScreen
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    applicationContext: Context,
    lifecycleScope: LifecycleCoroutineScope,
    activity: Activity,
    appViewModel: AppViewModel = viewModel(),
) {

    val mapCreatorViewModel = viewModel<MapCreatorViewModel>()
    val signInState by appViewModel.signInState.collectAsStateWithLifecycle()
    val emailAuthClient = EmailAuthClient(appViewModel.auth, viewModel.database)
    val googleAuthUiClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            appViewModel.auth
        )
    }
    val navController = rememberNavController()
    val mapViewModel = MapViewModel()


    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(Screen.StartScreen.route) {
            LaunchedEffect(key1 = Unit) {
                if (appViewModel.checkIfLogged())
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
                            appViewModel.onSignIn(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = signInState.signInSuccessful){
                if (signInState.signInSuccessful){
                    val user = appViewModel.auth.currentUser
                    appViewModel.database.collection("users")
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
                                        .document(user.email!!)
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
            val locationPermissionViewModel = viewModel<LocationPermissionViewModel>()
            val permissionDialogVisible by locationPermissionViewModel.shouldDialogBeVisible.collectAsState()
            val locationPermissionsLauncher =  rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    val areGranted = permissions.values.reduce { accumulator, permission ->
                        accumulator && permission
                    }
                    if (areGranted) {
                        navController.navigate(Screen.MapCreatorScreen1.route)
                    } else {
                        locationPermissionViewModel.setDialogVisibility(true)
                    }
                })

            if (permissionDialogVisible) {
                LocationPermissionDialog(
                    permanentlyDeclined = !activity.shouldShowRequestPermissionRationale(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                    ),
                    onGranted = {
                        locationPermissionViewModel.setDialogVisibility(false)
                        navController.navigate(Screen.MapCreatorScreen1.route) },
                    onGoToAppSettings = {
                        locationPermissionViewModel.setDialogVisibility(false)
                        (Activity::openAppSetting)(activity) },
                    onDismiss = {
                        locationPermissionViewModel.setDialogVisibility(false)
                    }
                )
            }
            LaunchedEffect(key1 = Unit) {
                viewModel.viewModelScope.launch {
                    viewModel.getSignedUserInfo()
                }
            }

            HomeScreen(
                appViewModel = viewModel,
                onCreateMap = {
                    locationPermissionViewModel.checkAndRequestPermissions(
                        context = applicationContext,
                        navController = navController,
                        screen = Screen.MapCreatorScreen1.route,
                        permissions = locationPermissionViewModel.locationPermissions,
                        launcher = locationPermissionsLauncher
                    )
                },
                onSignOut = {
                    lifecycleScope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(
                            applicationContext,
                            applicationContext.resources.getString(R.string.toast_sign_out),
                            Toast.LENGTH_LONG
                        ).show()
                        navController.popBackStack(Screen.StartScreen.route, false)
                    }
                },
                onOpenMap = {
                    viewModel.switchDisplayedMap(it)
                    locationPermissionViewModel.checkAndRequestPermissions(
                        context = applicationContext,
                        navController = navController,
                        screen = Screen.MapScreen.route,
                        permissions = locationPermissionViewModel.locationPermissions,
                        launcher = locationPermissionsLauncher
                    )
                },
            )
        }
        composable(route = Screen.MapScreen.route) {
            MapScreen(
                mapViewModel,
                viewModel.getDisplayedMapInfo(),
                navController
            )
        }

        composable(route = Screen.EmailSignUpScreen.route){
            LaunchedEffect(key1 = signInState.signInSuccessful){
                if (signInState.signInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.resources.getString(R.string.toast_sign_up),
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

            EmailSignInScreen(
                navController = navController,
                emailAuthClient = emailAuthClient,
                onSignInClick = { viewModel.onSignIn(it) }
            )
        }
        composable(route = Screen.MapCreatorScreen1.route) {
            MapCreatorScreen1(
                mapCreatorViewModel,
                navController,
            )
        }
        composable(route = Screen.MapCreatorScreen2.route) {
            MapCreatorScreen2(
                mapCreatorViewModel,
                navController,
            )
        }
        composable(route = Screen.MapCreatorScreen3.route) {
            MapCreatorScreen3(
                mapCreatorViewModel,
                navController,
            )
        }

        composable(route = Screen.CreditsScreen.route) {
            CreditsScreen(onGoBack = { navController.popBackStack(
                Screen.StartScreen.route,
                false
            )})
        }

        composable(route = Screen.MarkerConfigurationScreen.route) {
            MarkerConfigurationScreen(
                mapCreatorViewModel = mapCreatorViewModel,
                navController = navController
            )
        }
        composable(route = Screen.FloorConfigurationScreen.route) {
            FloorConfigurationScreen(
                navController = navController,
                mapCreatorViewModel = mapCreatorViewModel
            )
        }
    }
}
