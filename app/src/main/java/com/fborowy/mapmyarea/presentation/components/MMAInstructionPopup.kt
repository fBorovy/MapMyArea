package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.fborowy.mapmyarea.ui.theme.InstructionPopupGray
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMAInstructionPopup(content: String, onDismiss: () -> Unit) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onDismiss()
                }
                .padding(horizontal = 70.dp, vertical = 200.dp)
                .clip(RoundedCornerShape(25.dp))
                .border(width = 2.dp, shape = RoundedCornerShape(25.dp), color = TextWhite)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, InstructionPopupGray),
                    )
                )
        ) {
            Text(
                text = "?",
                style = Typography.titleLarge,
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.TopCenter)
            )
            Text(
                text = content,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 70.dp, horizontal = 25.dp)
            )
        }
    }
}