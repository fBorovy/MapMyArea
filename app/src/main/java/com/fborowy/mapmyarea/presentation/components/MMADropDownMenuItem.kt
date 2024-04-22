package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fborowy.mapmyarea.ui.theme.DropDownMenuGray
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMADropDownMenuItem(
    label: String,
    onItemClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                label,
                style = Typography.bodySmall
            )
        },
        onClick = { onItemClick() },
        modifier = Modifier
            .background(DropDownMenuGray)
    )
}