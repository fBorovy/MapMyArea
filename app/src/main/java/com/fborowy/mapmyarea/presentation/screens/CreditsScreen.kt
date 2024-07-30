package com.fborowy.mapmyarea.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.presentation.components.ClickableLink
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun CreditsScreen(onGoBack: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, TextWhite),
                    )
                )
                .padding(vertical = 33.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.credits), style = Typography.titleLarge)
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .padding(start = 15.dp)
                    .clickable { onGoBack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_40),
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
        Text(
            text = stringResource(id = R.string.flaticon),
            style = Typography.labelLarge,
            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
        )
        ClickableLink(link = "https://www.flaticon.com/free-icons/block", additionalText = "Freepik; ")
        ClickableLink(link = "https://www.flaticon.com/free-icons/parking", additionalText = "Freepik; ")
        ClickableLink(link = "https://www.flaticon.com/free-icons/other", additionalText = "Anggara; ")
    }
}