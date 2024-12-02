package com.gami.tomokanjimobile.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme

@Composable
fun BottomNavItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val textColor = if (isSelected) CustomTheme.colors.textPrimary else CustomTheme.colors.textSecondary
    Text(
        text = title,
        color = textColor,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun Nav(navController: NavController, currentRoute: String?) {
    val borderColor = CustomTheme.colors.secondary
    val screens = listOf("home", "kanji", "words")

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
                title = screen.capitalize(),
                isSelected = currentRoute == screen,
                onClick = { navController.navigate(screen) }
            )
        }
    }
}