package com.gami.tomokanjimobile.ui.composables.words

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.network.WordApi
import kotlinx.coroutines.launch

@Composable
fun WordDetail(word: Word,
                isMastered: Boolean,
                navController: NavController,
                viewModel: WordViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val borderColor = if (isMastered) CustomTheme.colors.primary else CustomTheme.colors.backgroundSecondary
        Box(
            modifier = Modifier
                .height(400.dp)
                .width(300.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CustomTheme.colors.backgroundSecondary)
                .border(
                    2.dp,
                    borderColor,
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (word.kanjis.isNotEmpty()) {
                        Text(
                            text = word.kanjis[0].text,
                            fontSize = 24.sp,
                            color = CustomTheme.colors.textPrimary
                        )
                        Text(
                            text = word.kanas[0].text,
                            fontSize = 18.sp,
                            color = CustomTheme.colors.textSecondary
                        )
                    } else {
                        Text(
                            text = word.kanas[0].text,
                            fontSize = 24.sp,
                            color = CustomTheme.colors.textPrimary
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Level: N${word.level}",
                        fontSize = 16.sp,
                        color = CustomTheme.colors.textSecondary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Meanings:",
                        fontSize = 18.sp,
                        color = CustomTheme.colors.textPrimary
                    )
                    Text(
                        text = word.translations.joinToString(", "),
                        fontSize = 16.sp,
                        color = CustomTheme.colors.textSecondary
                    )
                }
            }
        }

        Button(
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomTheme.colors.primary,
                contentColor = Color.White
            ),
            onClick = {
                // Toggle mastery status immediately for the UI
                val newMasteryStatus = !isMastered
                viewModel.updateWordMastery(word.id, newMasteryStatus)

                // Update the server in the background
                coroutineScope.launch {
                    if (newMasteryStatus) {
                        WordApi.service.masterWord(1, word.id)
                    } else {
                        WordApi.service.unmasterWord(1, word.id)
                    }
                }

                // Navigate back instantly
                navController.popBackStack()
            }
        ) {
            Text(if (isMastered) "Unmaster" else "Master")
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = CustomTheme.colors.primary
            ),
            onClick = { navController.popBackStack() },

            ) {
            Text("Back")
        }
    }
}