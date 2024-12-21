package com.gami.tomokanjimobile.ui.composables.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiViewModel
import com.gami.tomokanjimobile.ui.composables.words.WordViewModel

@Composable
fun HomeScreen(sharedViewModel: SharedViewModel, kanjiViewModel: KanjiViewModel, wordViewModel: WordViewModel, navController: NavController, context: Context) {
    val isKanjiLoading by kanjiViewModel.isLoading.collectAsState()
    val isWordLoading by wordViewModel.isLoading.collectAsState()

    val isLoading = isKanjiLoading || isWordLoading

    Column(
        Modifier.fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .background(CustomTheme.colors.backgroundPrimary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Dashboard",
                        fontSize = 24.sp,
                        color = CustomTheme.colors.textPrimary,
                        modifier = Modifier
                            .padding(top = 60.dp, start = 16.dp, bottom = 16.dp)
                    )
                }
            }

            if(!isLoading) {
                val kanjiCount = sharedViewModel.kanjis.value.size
                val wordCount = sharedViewModel.words.value.size
                val masteredKanjiCount = sharedViewModel.kanjis.value.count { (_, mastered) -> mastered }
                val masteredWordCount = sharedViewModel.words.value.count { (_, mastered) -> mastered }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CustomTheme.colors.backgroundPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(8.dp)
                                .background(CustomTheme.colors.backgroundSecondary, RoundedCornerShape(8.dp))
                                .clickable { navController.navigate("kanji") }
                        )
                        {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            )
                            {
                                Text(
                                    text = "Kanjis",
                                    fontSize = 18.sp,
                                    color = CustomTheme.colors.textPrimary,
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .clip(RoundedCornerShape(8.dp)) // Apply rounded corners
                                        .background(CustomTheme.colors.textSecondary) // Background for track
                                ) {
                                    LinearProgressIndicator(
                                        progress = {masteredKanjiCount.toFloat() / kanjiCount.toFloat()},
                                        color = CustomTheme.colors.primary,
                                        trackColor = Color.Transparent, // Make track transparent (handled by Box background)
                                        modifier = Modifier.fillMaxWidth() // Ensure it takes the full width
                                    )
                                }
                                Row{
                                    Column(modifier = Modifier.weight(1f))
                                    {
                                        Text(
                                            text = "$masteredKanjiCount / $kanjiCount",
                                            fontSize = 14.sp,
                                            color = CustomTheme.colors.textPrimary,
                                        )
                                    }
                                    Column()
                                    {
                                        Text(
                                            text = "${"%.2f".format((masteredKanjiCount.toFloat() / kanjiCount.toFloat())*100)}%",
                                            fontSize = 14.sp,
                                            color = CustomTheme.colors.textSecondary,
                                        )
                                    }
                                }

                            }
                        }
                        Row(
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(8.dp)
                                .background(CustomTheme.colors.backgroundSecondary, RoundedCornerShape(8.dp))
                                .clickable { navController.navigate("word") }
                        )
                        {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            )
                            {
                                Text(
                                    text = "Words",
                                    fontSize = 18.sp,
                                    color = CustomTheme.colors.textPrimary,
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .clip(RoundedCornerShape(8.dp)) // Apply rounded corners
                                        .background(CustomTheme.colors.textSecondary) // Background for track
                                ) {
                                    LinearProgressIndicator(
                                        progress = {masteredWordCount.toFloat() / wordCount.toFloat()},
                                        color = CustomTheme.colors.primary,
                                        trackColor = Color.Transparent, // Make track transparent (handled by Box background)
                                        modifier = Modifier.fillMaxWidth() // Ensure it takes the full width
                                    )
                                }
                                Row{
                                    Column(modifier = Modifier.weight(1f))
                                    {
                                        Text(
                                            text = "$masteredWordCount / $wordCount",
                                            fontSize = 14.sp,
                                            color = CustomTheme.colors.textPrimary,
                                        )
                                    }
                                    Column()
                                    {
                                        Text(
                                            text = "${"%.2f".format((masteredWordCount.toFloat() / wordCount.toFloat())*100)}%",
                                            fontSize = 14.sp,
                                            color = CustomTheme.colors.textSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CustomTheme.colors.backgroundPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        fontSize = 24.sp,
                        color = CustomTheme.colors.textPrimary,
                    )
                }
            }
        }
    }
}