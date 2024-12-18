package com.gami.tomokanjimobile.ui.composables.words

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun WordDetail(
    word: Word,
    isMastered: Boolean,
    navController: NavController,
    viewModel: WordViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center // Keeps content vertically centered
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // Enables vertical scrolling
                .wrapContentHeight()
                .padding(16.dp), // Adjusts spacing from screen borders
            horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
        ) {
            val borderColor = if (isMastered) CustomTheme.colors.primary else CustomTheme.colors.backgroundSecondary

            // The card wrapper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
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
                    verticalArrangement = Arrangement.Top,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Display the kanjis and kanas
                        if (word.kanjis.isNotEmpty()) {
                            Text(
                                text = word.kanjis[0].text,
                                fontSize = 24.sp,
                                color = CustomTheme.colors.textPrimary
                            )
                            if(word.kanas.isNotEmpty()) {
                                Text(
                                    text = word.kanas[0].text,
                                    fontSize = 18.sp,
                                    color = CustomTheme.colors.textSecondary
                                )
                            }
                        } else if (word.kanas.isNotEmpty()) {
                            Text(
                                text = word.kanas[0].text,
                                fontSize = 24.sp,
                                color = CustomTheme.colors.textPrimary
                            )
                        }
                        else {
                            Text(
                                text = "No readings available",
                                fontSize = 24.sp,
                                color = CustomTheme.colors.textPrimary
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Level: N${word.level}",
                            fontSize = 16.sp,
                            color = CustomTheme.colors.textSecondary
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Meanings:",
                            fontSize = 18.sp,
                            color = CustomTheme.colors.textPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        word.translations.forEach { meaning ->
                            Text(
                                text = "• $meaning",
                                fontSize = 16.sp,
                                color = CustomTheme.colors.textSecondary,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Buttons
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
}