package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.data.Kanji

@Composable
fun KanjiCard(kanji: Kanji, mastered: Boolean, onClick: () -> Unit, showKunyomi: Boolean) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CustomTheme.colors.backgroundSecondary)
            .border(
                2.dp,
                if (mastered) CustomTheme.colors.primary else CustomTheme.colors.backgroundSecondary,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = kanji.character, fontSize = 28.sp, color = CustomTheme.colors.textPrimary)
            if (showKunyomi && kanji.kunyomi.isNotEmpty()) {
                Text(text = kanji.kunyomi.first(), fontSize = 14.sp, color = CustomTheme.colors.textPrimary)
            }
        }
    }
}