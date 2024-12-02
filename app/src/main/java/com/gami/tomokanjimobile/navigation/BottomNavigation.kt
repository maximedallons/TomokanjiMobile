package com.gami.tomokanjimobile.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.MainActivity

@Composable
fun BottomNavItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val textColor = if (isSelected) CustomTheme.colors.textPrimary else CustomTheme.colors.textSecondary

    Text(
        text = title,
        modifier = Modifier.clickable(onClick = onClick),
        color = textColor,
        textAlign = TextAlign.Center
    )
}

@Composable
fun Nav(selectedScreen: MutableState<MainActivity.Screen>, context: android.content.Context) {
    val borderColor = CustomTheme.colors.secondary
    val screens = listOf(
        MainActivity.Screen.Home(context),
        MainActivity.Screen.Kanji(context),
        MainActivity.Screen.Words(context)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CustomTheme.colors.backgroundSecondary)
            .padding(vertical = 24.dp)
            .drawWithContent {
                drawLine(
                    color = borderColor,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(0f, -24.dp.toPx()),
                    end = Offset(size.width, -24.dp.toPx())
                )
                drawContent()
            },
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        screens.forEach { screen ->
            BottomNavItem(
                title = screen.name,
                isSelected = selectedScreen.value == screen,
                onClick = { selectedScreen.value = screen }
            )
        }
    }
}