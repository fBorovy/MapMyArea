package com.fborowy.mapmyarea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.InstructionPopupGray
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MMANewFloorPopup(level: Int, onCancel: () -> Unit) {


    Dialog(
        onDismissRequest = onCancel,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 110.dp)
                .clip(RoundedCornerShape(25.dp))
                .border(width = 2.dp, shape = RoundedCornerShape(25.dp), color = TextWhite)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black, InstructionPopupGray),
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.new_floor_popup_title),
                style = Typography.titleMedium,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 10.dp)
            )
            Text(
                text = stringResource(id = R.string.floor_level) + level,
                style = Typography.titleSmall
            )
        }
    }

}