package com.fborowy.mapmyarea.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.ui.theme.TextWhite
import com.fborowy.mapmyarea.ui.theme.Typography

@Composable
fun MapCreatorScreen(
    viewModel: ViewModel,
    navController: NavController
) {
    val context = LocalContext.current

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
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .clickable { navController.popBackStack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_40),
                    contentDescription = context.getString(R.string.go_back),
                )
            }
            Text(context.resources.getString(R.string.map_creator_screen_title), style = Typography.titleMedium, modifier = Modifier.padding(horizontal = 84.dp))
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterEnd)
                    .padding(end = 20.dp)
                    .size(44.dp)
                    .border(1.dp, TextWhite, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "?", style = Typography.titleLarge)
            }
        }
        



    }
}