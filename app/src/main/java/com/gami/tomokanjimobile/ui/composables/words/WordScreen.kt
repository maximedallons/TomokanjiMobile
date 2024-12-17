package com.gami.tomokanjimobile.ui.composables.words

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.ui.composables.LevelSelectionHeader
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gami.tomokanjimobile.dao.WordDatabaseBuilder
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton

@Composable
fun WordScreen(
    viewModel: WordViewModel = viewModel(),
    navController: NavController,
    circleButtonsState: MutableState<List<CircleButton>>,
    context: Context
) {
    val wordDao = WordDatabaseBuilder.getInstance(context).wordDao()
    val coroutineScope = rememberCoroutineScope()

    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val defaultColor = CustomTheme.colors.backgroundSecondary
    val primaryColor = CustomTheme.colors.primary

    LaunchedEffect(currentLevel) {
        circleButtonsState.value = List(5) { index ->
            val level = 5 - index // Reverse the order
            val color = if (level == currentLevel) primaryColor else defaultColor
            CircleButton(
                label = "N$level",
                onClick = { viewModel.updateCurrentLevel(level) },
                background = color
            )
        }

        if(viewModel.needsUpdate.value) {
            viewModel.fetchWordsForLevel(wordDao, currentLevel)
            viewModel.updateNeedsUpdate(false)
        }
    }

    Column {
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
            WordList(
                viewModel = viewModel,
                navController = navController,
                coroutineScope = coroutineScope
            )
        }
    }
}