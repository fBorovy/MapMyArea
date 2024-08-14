package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMAHeader(
    header: String,
    onGoBack: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (onGoBack != null) {
            Box(
                modifier = Modifier
                    .clickable { onGoBack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_40),
                    contentDescription = stringResource(id = R.string.go_back),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
        Text(
            text = header,
            style = Typography.titleLarge,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}