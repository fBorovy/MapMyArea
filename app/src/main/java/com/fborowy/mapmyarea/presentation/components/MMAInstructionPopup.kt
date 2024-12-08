package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMAInstructionPopup(
    title: String? = null,
    content: String,
    onDismiss: () -> Unit,
    onDismissLabel: String = stringResource(id = R.string.ok),
    onConfirm: (() -> Unit)? = null,
    onConfirmLabel: String = stringResource(id = R.string.confirm)
) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        ),
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(50.dp)
                .shadow(elevation = 7.dp, shape = RoundedCornerShape(25.dp))
                .clip(RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(25.dp),
                    color = MaterialTheme.colorScheme.onTertiary
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title ?: "?",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = content,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 24.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MMAButton(text = onDismissLabel, onClick = { onDismiss() })
                if (onConfirm != null)
                    MMAButton(text = onConfirmLabel, onClick = { onConfirm() })
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = true) { }
    )
}