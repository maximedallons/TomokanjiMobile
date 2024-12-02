package com.gami.tomokanjimobile.ui.composables.kanjis

import android.content.Context
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.dao.KanjiDatabaseBuilder
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.network.KanjiApi
import com.gami.tomokanjimobile.utils.Converters
import kotlinx.coroutines.launch

@Composable
fun KanjiDetail(kanji: Kanji, isMastered: Boolean, navController: NavController, context: Context) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                coroutineScope.launch {
                    if(isMastered) {
                        KanjiApi.service.unmasterKanji(1, kanji.id)
                    } else {
                        KanjiApi.service.masterKanji(1, kanji.id)
                    }
                }
                navController.navigate("kanji")
            }
        ) {
            Text(if (isMastered) "Unmaster" else "Master")
        }

        Button(
            onClick = { navController.popBackStack() }
        ) {
            Text("Back")
        }
    }
}