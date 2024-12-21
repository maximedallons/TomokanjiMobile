package com.gami.tomokanjimobile.ui.composables.words

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.data.Word

@Composable
fun WordList(
    viewModel: WordViewModel,
    navController: NavController
) {
    val words by viewModel.filteredWords.collectAsState()
    val showKanas by viewModel.showKanas.collectAsState()
    val showTranslations by viewModel.showTranslations.collectAsState()

    // Lazy Grid State to control the word list scrolling
    val listState = rememberLazyGridState()

    // Process the word list
    val wordGridItems: List<WordGridItem> = remember(words) {
        val items = mutableListOf<WordGridItem>()
        words.forEachIndexed { index, word ->
            val altIndex = index + 1
            if (altIndex == 1) {
                val firstLimit = if (words.size < 100) words.size else 100
                items.add(WordGridItem.Divider("$altIndex - $firstLimit"))
            }
            if (altIndex % 100 == 0 && altIndex != words.size) {
                val nextLimit = if (altIndex + 100 < words.size) altIndex + 100 else words.size
                items.add(WordGridItem.Divider("${altIndex + 1} - $nextLimit"))
            }
            items.add(WordGridItem.WordItemWord(word))
        }
        items
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main word list
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns in the grid
                state = listState,
                contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 160.dp),
                modifier = Modifier
                    .weight(0.85f)
            ) {
                items(
                    count = wordGridItems.size,
                    span = { index ->
                        when (wordGridItems[index]) {
                            is WordGridItem.Divider -> GridItemSpan(2) // Dividers span across 2 columns
                            is WordGridItem.WordItemWord -> GridItemSpan(1) // Word items take 1 column
                        }
                    }
                ) { index ->
                    when (val item = wordGridItems[index]) {
                        is WordGridItem.WordItemWord -> {
                            val (word, mastered) = item.wordData
                            WordCard(
                                word = word,
                                mastered = mastered,
                                onClick = {
                                    navController.navigate("word_detail/${word.id}") {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                inclusive = true
                                            }
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                showKanas = showKanas,
                                showTranslations = showTranslations
                            )
                        }

                        is WordGridItem.Divider -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(CustomTheme.colors.backgroundPrimary)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.rangeText,
                                    fontSize = 20.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class WordGridItem {
    data class WordItemWord(val wordData: Pair<Word, Boolean>) : WordGridItem()
    data class Divider(val rangeText: String) : WordGridItem()
}