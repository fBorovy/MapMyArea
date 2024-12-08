package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMAButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconResource: Int? = null,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val isIcon = (iconResource != null)
    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = if (isIcon) 7.dp else 13.dp, vertical = 7.dp)
    ) {
        if (iconResource == null) {
            Text(
                text = text,
                style = Typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        } else {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(iconResource),
                contentDescription = text,
                tint = textColor,
            )
        }
    }
}