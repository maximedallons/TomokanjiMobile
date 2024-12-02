package com.gami.tomokanjimobile.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gami.tomokanji.ui.theme.CustomTheme

@Composable
fun LevelSelectionHeader(selectedLevel: Int, onSelected: (Int) -> Unit) {
    val borderColor = CustomTheme.colors.secondary
    Row(
        modifier = Modifier
            .background(CustomTheme.colors.backgroundSecondary)
            .fillMaxWidth()
            .padding(top = 48.dp)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = borderColor,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            },

        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (level in 5 downTo 1) {
            val isSelected = level == selectedLevel
            Button(
                onClick = { onSelected(level) },
                modifier = Modifier
                    .padding(4.dp)
                    .size(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) CustomTheme.colors.primary else CustomTheme.colors.backgroundSecondary,
                    contentColor = Color.White
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "N$level",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}