package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMATextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isHidden: Boolean,
    enabled: Boolean = true,
    textStyle: TextStyle = Typography.bodyMedium,
    focusedColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    val focusManager = LocalFocusManager.current

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
        focusedTextColor = focusedColor,
        focusedPlaceholderColor = focusedColor,
        focusedBorderColor = focusedColor,
        cursorColor = focusedColor,
    )

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { 
                      Text(text = placeholder, style = textStyle)
                      },
        visualTransformation = if (isHidden) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = textFieldColors,
        textStyle = textStyle,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        enabled = enabled,
    )
}