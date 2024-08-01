package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.ui.theme.TextWhite

@Composable
fun MMAButton(
    text: String,
    style: TextStyle,
    horizontalPadding: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .border(1.dp, TextWhite, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = horizontalPadding)
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = text,
            style = style
        )
    }
}