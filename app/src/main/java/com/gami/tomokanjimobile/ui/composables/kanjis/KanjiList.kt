package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.R
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.ui.composables.navigation.DividerLinks
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun KanjiList(
    viewModel: KanjiViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val kanjis by viewModel.kanjis.collectAsState()
    val showKunyomi by viewModel.showKunyomi.collectAsState()
    val listState = rememberLazyGridState()

    // Monitor the current first visible item in the list
    val currentDividerIndex by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val visibleIndex = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            visibleIndex
        }
    }

    // Process the Kanji list and create a grid with dividers
    val kanjiGridItems: List<KanjiGridItem> = remember(kanjis) {
        val items = mutableListOf<KanjiGridItem>()
        kanjis.forEachIndexed { index, kanji ->
            val altIndex = index + 1
            if (altIndex == 1) {
                val firstLimit = if (kanjis.size < 100) kanjis.size else 100
                items.add(KanjiGridItem.Divider("$altIndex - $firstLimit"))
            }
            if (altIndex % 100 == 0 && altIndex != kanjis.size) {
                val nextLimit = if (altIndex + 100 < kanjis.size) altIndex + 100 else kanjis.size
                items.add(KanjiGridItem.Divider("${altIndex + 1} - $nextLimit"))
            }
            items.add(KanjiGridItem.KanjiItem(kanji))
        }
        items
    }

    // Map dividers to their positions for the sidebar
    val dividers: List<Pair<String, Int>> = kanjiGridItems.mapIndexedNotNull { index, item ->
        if (item is KanjiGridItem.Divider) Pair(item.rangeText.split("-")[0].trim(), index) else null
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
            // Main kanji grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columns in the grid
                state = listState,
                contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 80.dp),
                modifier = Modifier
                    .weight(0.85f)
            ) {
                items(
                    count = kanjiGridItems.size,
                    span = { index ->
                        when (kanjiGridItems[index]) {
                            is KanjiGridItem.Divider -> GridItemSpan(2) // Dividers span across 2 columns
                            is KanjiGridItem.KanjiItem -> GridItemSpan(1) // Kanji items take 1 column
                        }
                    }
                ) { index ->
                    when (val item = kanjiGridItems[index]) {
                        is KanjiGridItem.KanjiItem -> {
                            val (kanji, mastered) = item.kanji
                            KanjiCard(
                                kanji = kanji,
                                mastered = mastered,
                                onClick = {
                                    navController.navigate("kanji_detail/${Json.encodeToString(kanji)}/${mastered}")
                                },
                                showKunyomi = showKunyomi
                            )
                        }

                        is KanjiGridItem.Divider -> {
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

            // Sidebar with dividers
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.10f)
                    .background(CustomTheme.colors.backgroundPrimary)
            ) {
                DividerLinks(
                    dividers = dividers,
                    currentDividerIndex = currentDividerIndex,
                    listState = listState,
                    coroutineScope = coroutineScope
                )
            }
        }

        // Floating Action Button for toggling Kunyomi
        FloatingActionButton(
            onClick = { viewModel.toggleShowKunyomi() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = CustomTheme.colors.secondary,
            contentColor = CustomTheme.colors.textPrimary
        ) {
            val iconResource = if (showKunyomi) {
                R.drawable.eye_open
            } else {
                R.drawable.eye_closed
            }

            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = iconResource),
                tint = CustomTheme.colors.textPrimary,
                contentDescription = "Toggle Kunyomi"
            )
        }
    }
}

sealed class KanjiGridItem {
    data class KanjiItem(val kanji: Pair<Kanji, Boolean>) : KanjiGridItem()
    data class Divider(val rangeText: String) : KanjiGridItem()
}