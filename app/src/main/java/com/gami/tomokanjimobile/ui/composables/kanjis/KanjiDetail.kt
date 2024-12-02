package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.data.Kanji

@Composable
fun KanjiDetail(kanji: Kanji, isMastered: Boolean, onBack: () -> Unit, onToggleMastered: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BackHandler {
            onBack()
        }

        Text(
            text = kanji.character,
            color = CustomTheme.colors.textPrimary,
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(text = "Level: N${kanji.level}", color = CustomTheme.colors.textSecondary)
        Text(text = "Kunyomi: ${kanji.kunyomi.joinToString(", ")}", color = CustomTheme.colors.textPrimary)
        Text(text = "Onyomi: ${kanji.onyomi.joinToString(", ")}", color = CustomTheme.colors.textPrimary)
        Text(text = "Meanings: ${kanji.meanings.joinToString(", ")}", color = CustomTheme.colors.textPrimary)

        Button(
            modifier = Modifier
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomTheme.colors.primary,
                contentColor = Color.White
            ),
            onClick = {
                onToggleMastered(!isMastered)
            }
        ) {
            Text(if (isMastered) "Unmaster" else "Master")
        }

        // Back button to return to the list
        Text(
            text = "Back",
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { onBack() },
            color = CustomTheme.colors.primary
        )
    }
}