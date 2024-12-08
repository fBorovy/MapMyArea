package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMAHeader(
    header: String,
    onGoBack: (() -> Unit)? = null,
    onMoreInfo: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (onGoBack != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onGoBack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = stringResource(id = R.string.go_back),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = header,
                style = Typography.titleLarge,
                color = textColor,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = if (onGoBack != null) 30.dp else 0.dp),// start = if (header.length > 19) 45.dp else 0.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onMoreInfo != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onMoreInfo() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.more_info),
                    contentDescription = stringResource(id = R.string.show_description),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }

    }
}