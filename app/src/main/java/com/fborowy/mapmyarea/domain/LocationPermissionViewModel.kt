package com.fborowy.mapmyarea.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocationPermissionViewModel: ViewModel() {

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val _shouldDialogBeVisible = MutableStateFlow(false)
    val shouldDialogBeVisible: StateFlow<Boolean> = _shouldDialogBeVisible.asStateFlow()
    fun setDialogVisibility(flag: Boolean) {
        _shouldDialogBeVisible.update {
            flag
        }
    }

    private fun checkPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkAndRequestPermissions(
        context: Context,
        navController: NavController,
        screen: String,
        permissions: Array<String>,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
    ) {
        if ( checkPermissions(context)) {
            navController.navigate(screen)
        } else {
            launcher.launch(permissions)
        }
    }


}