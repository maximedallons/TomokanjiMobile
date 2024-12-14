package com.gami.tomokanjimobile.ui.composables.words

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.ui.composables.navigation.ScrollToTopFAB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun WordList(
    viewModel: WordViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val words by viewModel.words.collectAsState()
    val showKanas by viewModel.showKanas.collectAsState()
    val showTranslations by viewModel.showTranslations.collectAsState()

    // Lazy Grid State to control the word list scrolling
    val listState = rememberLazyGridState()

    // Monitor the current first visible item in the list
    val currentDividerIndex by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val visibleIndex = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            visibleIndex
        }
    }

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

    // Map dividers to their positions with simplified text for the sidebar
    val dividers: List<Pair<String, Int>> = wordGridItems.mapIndexedNotNull { index, item ->
        if (item is WordGridItem.Divider) Pair(item.rangeText.split("-")[0].trim(), index) else null
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
                contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 80.dp),
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
                                    navController.navigate("word_detail/${Json.encodeToString(word)}/${mastered}")
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
                                    .background(CustomTheme.colors.backgroundSecondary)
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

            // Sidebar with dividers
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.10f)
                    .background(CustomTheme.colors.backgroundSecondary)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(dividers.size) { index ->
                        val divider = dividers[index]
                        val isActive = index == dividers.indexOfLast { currentDividerIndex >= it.second }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        // Scroll to the position of the divider
                                        listState.scrollToItem(divider.second)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Conditional styling to change the view based on whether it's active or not
                            if (isActive) {
                                // Active Divider with Circle Shape
                                Box(
                                    modifier = Modifier
                                        .size(30.dp, 18.dp) // Circle size
                                        .background(
                                            color = CustomTheme.colors.primary,
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = divider.first,
                                        fontSize = 12.sp,
                                        color = CustomTheme.colors.textPrimary // Text inside the circle
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp, 18.dp) // Circle size
                                        .background(
                                            color = CustomTheme.colors.backgroundSecondary,
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = divider.first,
                                        fontSize = 12.sp,
                                        color = CustomTheme.colors.textPrimary // Text inside the circle
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

sealed class WordGridItem {
    data class WordItemWord(val wordData: Pair<Word, Boolean>) : WordGridItem()
    data class Divider(val rangeText: String) : WordGridItem()
}