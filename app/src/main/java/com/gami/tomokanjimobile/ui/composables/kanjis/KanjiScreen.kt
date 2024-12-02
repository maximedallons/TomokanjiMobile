package com.gami.tomokanjimobile.ui.composables.kanjis

import KanjiViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.dao.KanjiDatabaseBuilder
import com.gami.tomokanjimobile.ui.composables.LevelSelectionHeader
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun KanjiScreen(
    viewModel: KanjiViewModel = viewModel(),
    navController: NavController,
    context: Context
) {
    val kanjiDao = KanjiDatabaseBuilder.getInstance(context).kanjiDao()
    val coroutineScope = rememberCoroutineScope()

    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(currentLevel) {
        if(viewModel.needsUpdate.value) {
            viewModel.fetchKanjisForLevel(kanjiDao, currentLevel)
            viewModel.updateNeedsUpdate(false)
        }
    }

    Column {
        LevelSelectionHeader(currentLevel) { selectedLevel ->
            viewModel.updateCurrentLevel(selectedLevel)
        }
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(CustomTheme.colors.backgroundPrimary),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CustomTheme.colors.primary)
            }
        } else {
            KanjiList(
                viewModel = viewModel,
                navController = navController,
                coroutineScope = coroutineScope
            )
        }
    }
}