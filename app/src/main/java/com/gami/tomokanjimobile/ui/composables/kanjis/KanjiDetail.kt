package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.network.KanjiApi
import com.gami.tomokanjimobile.network.WordApi
import kotlinx.coroutines.launch

@Composable
fun KanjiDetail(id : Int,
                navController: NavController,
                sharedViewModel: SharedViewModel,
                viewModel: KanjiViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val kanji = viewModel.fetchKanjiById(id)
    var mastered by remember { mutableStateOf(viewModel.isMastered(id)) }

    LaunchedEffect(mastered) {}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val borderColor = if (mastered) {
                CustomTheme.colors.primary
            } else {
                CustomTheme.colors.backgroundSecondary
            }

            // Card wrapper with new buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.backgroundSecondary)
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back button in the top-left
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CustomTheme.colors.textPrimary
                        )
                    }

                    // Master button in the top-right
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        onClick = {
                            mastered = !mastered
                            viewModel.updateKanjiMastery(kanji.id, mastered)

                            // Update the server in the background
                            coroutineScope.launch {
                                if (mastered) {
                                    KanjiApi.service.masterKanji(1, kanji.id)
                                } else {
                                    KanjiApi.service.unmasterKanji(1, kanji.id)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = if (mastered) "Unmaster" else "Master",
                            tint = if (mastered) CustomTheme.colors.primary else CustomTheme.colors.textPrimary
                        )
                    }

                    // Main content inside the card
                    Column(
                        modifier = Modifier.padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                        {
                            Text(
                                text = kanji.character,
                                fontSize = 48.sp,
                                color = CustomTheme.colors.textPrimary
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp))
                        {
                            Column(modifier = Modifier.weight(1f))
                            {
                                Text(
                                    text = "Onyomi",
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                                Text(
                                    text = kanji.onyomi.joinToString(", "),
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textSecondary
                                )
                            }
                            Column()
                            {
                                Text(
                                    text = "Kunyomi",
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                                Text(
                                    text = kanji.kunyomi.joinToString(", "),
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textSecondary
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp))
                        {
                            Column(modifier = Modifier.weight(1f))
                            {
                                Text(
                                    text = "Meanings",
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                                Text(
                                    text = kanji.meanings.joinToString(", "),
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textSecondary
                                )
                            }
                            Column()
                            {
                                Text(
                                    text = "JLPT",
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                                Text(
                                    text = "N${kanji.level}",
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textSecondary
                                )
                            }
                        }

                        val matchingWords = viewModel.fetchWordsByKanjiText(kanji.character)
                            .sortedByDescending { it.level }

                        if (matchingWords.isNotEmpty()) {
                            Text(
                                text = "Related words",
                                fontSize = 16.sp,
                                color = CustomTheme.colors.textPrimary,
                                modifier = Modifier.padding(16.dp, 8.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp * minOf(matchingWords.size, 3)) // Set height to show only 3 items
                        ) {
                            LazyColumn {
                                items(matchingWords.size) { index ->
                                    val word = matchingWords[index]
                                    val isWordMastered = sharedViewModel.isWordMastered(word.id)
                                    val wordBorderColor = if (isWordMastered) {
                                        CustomTheme.colors.primary
                                    } else {
                                        CustomTheme.colors.backgroundTertiary
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp, 8.dp)
                                            .height(64.dp) // Set each item's height to 64.dp
                                            .border(2.dp, wordBorderColor, RoundedCornerShape(16.dp))
                                            .clickable { navController.navigate("word_detail/${word.id}") },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            if (word.kanjis.isNotEmpty()) {
                                                Text(
                                                    modifier = Modifier.padding(16.dp, 4.dp),
                                                    text = word.kanjis[0].text,
                                                    color = CustomTheme.colors.textPrimary,
                                                    fontSize = 16.sp
                                                )
                                            }
                                            if (word.kanas.isNotEmpty()) {
                                                Text(
                                                    modifier = Modifier.padding(16.dp, 4.dp),
                                                    text = word.kanas[0].text,
                                                    color = CustomTheme.colors.textSecondary,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = CustomTheme.colors.textPrimary,
                                            modifier = Modifier
                                                .padding(end = 16.dp) // Add padding on the right
                                                .align(Alignment.CenterVertically) // For vertical centering
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}