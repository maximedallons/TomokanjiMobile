package com.gami.tomokanjimobile.ui.composables.kanas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme

@Composable
fun KanaList(
    viewModel: KanaViewModel,
    navController: NavController
) {
    val hiraganas by viewModel.filteredHiraganas.collectAsState()
    val katakanas by viewModel.filteredKatakanas.collectAsState()
    val currentType by viewModel.currentType.collectAsState()
    val listState = rememberLazyGridState()

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
                columns = GridCells.Fixed(5), // 5 columns in the grid
                state = listState,
                contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 160.dp),
                modifier = Modifier
                    .weight(0.85f)
            ) {
                if(currentType == "H") {
                    items(hiraganas.size) { index ->
                        HiraganaCard(hiraganas[index].first, hiraganas[index].second, {
                            navController.navigate("kana_hiragana_detail/${hiraganas[index].first.id}") {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        inclusive = true
                                    }
                                }
                                launchSingleTop = true
                            }
                            viewModel.updateCurrentType("H")
                        })
                    }
                } else {
                    items(katakanas.size) { index ->
                        KatakanaCard(katakanas[index].first, katakanas[index].second, {
                            navController.navigate("kana_katakana_detail/${katakanas[index].first.id}") {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        inclusive = true
                                    }
                                }
                                launchSingleTop = true
                            }
                            viewModel.updateCurrentType("K")
                        })
                    }
                }
            }
        }
    }
}