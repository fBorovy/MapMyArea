package com.fborowy.mapmyarea.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.ButtonBlack

@Composable
fun LocationPermissionDialog(
    permanentlyDeclined: Boolean,
    onGranted: () -> Unit,
    onGoToAppSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = ButtonBlack,
        textContentColor = ButtonBlack,
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(onClick = {
                if (permanentlyDeclined)
                    onGoToAppSettings()
                else onGranted()
            }) {
                Text(text = if (permanentlyDeclined) stringResource(id = R.string.settings)
                else
                    stringResource(id = R.string.enable)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
        text = {
            Text(
                text =
                    if (permanentlyDeclined) stringResource(id = R.string.localisation_permanently_declined_dialog)
                    else  stringResource(id = R.string.localisation_permission_request)
            )
        }
    )
}