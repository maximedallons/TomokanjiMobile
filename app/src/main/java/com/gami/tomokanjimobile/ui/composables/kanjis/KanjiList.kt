package com.gami.tomokanjimobile.ui.composables.kanjis

import KanjiViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            state = listState
        ) {
            items(kanjis.size) { index ->
                val (kanji, mastered) = kanjis[index]
                KanjiCard(
                    kanji = kanji,
                    mastered = mastered,
                    onClick = {
                        navController.navigate("kanji_detail/${Json.encodeToString(kanji)}/${mastered}")
                    },
                    showKunyomi = showKunyomi
                )
            }
        }
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = CustomTheme.colors.secondary,
            contentColor = CustomTheme.colors.textPrimary
        ) {
            Icon(Icons.Filled.Home, contentDescription = "Scroll to Top")
        }

        FloatingActionButton(
            onClick = { viewModel.toggleShowKunyomi() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = CustomTheme.colors.secondary,
            contentColor = CustomTheme.colors.textPrimary
        ) {
            Icon(Icons.Filled.Done, contentDescription = "Toggle Kunyomi")
        }
    }
}