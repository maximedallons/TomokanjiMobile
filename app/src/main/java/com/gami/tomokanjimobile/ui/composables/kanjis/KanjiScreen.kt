package com.gami.tomokanjimobile.ui.composables.kanjis

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.dao.KanjiDatabaseBuilder
import com.gami.tomokanjimobile.ui.composables.LevelSelectionHeader
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton

@Composable
fun KanjiScreen(
    viewModel: KanjiViewModel = viewModel(),
    navController: NavController,
    circleButtonsState: MutableState<List<CircleButton>>,
    context: Context
) {
    val kanjiDao = KanjiDatabaseBuilder.getInstance(context).kanjiDao()
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
            viewModel.fetchKanjisForLevel(kanjiDao, currentLevel)
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
            println("Kanjis: ${viewModel.kanjis.value.size}")
            KanjiList(
                viewModel = viewModel,
                navController = navController,
                coroutineScope = coroutineScope
            )
        }
    }
}