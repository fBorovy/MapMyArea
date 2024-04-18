package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.ui.theme.ButtonBlack
import com.fborowy.mapmyarea.ui.theme.TextFieldGray

@Composable
fun MMATextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit),
    isHidden: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = placeholder,
        visualTransformation = if (isHidden) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .border(
                shape = RoundedCornerShape(15.dp),
                width = 2.dp,
                color = TextFieldGray
            ),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = TextFieldGray,
            unfocusedPlaceholderColor = TextFieldGray,
            focusedTextColor = ButtonBlack,
            focusedPlaceholderColor = ButtonBlack,
        ),
    )
}