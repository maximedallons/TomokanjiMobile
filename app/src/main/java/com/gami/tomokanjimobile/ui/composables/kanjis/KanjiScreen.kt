package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gami.tomokanjimobile.ui.composables.navigation.BottomNavigationViewModel
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton

@Composable
fun KanjiScreen(
    viewModel: KanjiViewModel = viewModel(),
    bottomNavigationViewModel: BottomNavigationViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()

    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val fetchProgress by viewModel.fetchProgress.collectAsState()

    val query by viewModel.query.collectAsState()

    val defaultColor = CustomTheme.colors.backgroundSecondary
    val selectedColor = CustomTheme.colors.primary

    LaunchedEffect(currentLevel) {
        bottomNavigationViewModel.clearCenterButtons()
        for(i in 5 downTo 1) {
            val color = if(i == currentLevel) selectedColor else defaultColor
            bottomNavigationViewModel.addCenterButton(
                CircleButton(
                    label = "N$i",
                    onClick = {
                        viewModel.updateCurrentLevel(i)
                    },
                    background = color
                )
            )
        }

        if(viewModel.query.value.isNotEmpty()) {
            viewModel.filterKanjisIds(viewModel.query.value)
        } else {
            viewModel.fetchKanjisForLevel(currentLevel)
        }
    }

    Column {
        Row (
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
                    text = "Kanjis",
                    fontSize = 24.sp,
                    color = CustomTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp, bottom = 16.dp)
                )

                TextField(
                    value = query,
                    onValueChange = { viewModel.filterKanjisIds(it) },
                    placeholder = { Text("Search for a kanji") }, // Use placeholder instead of label
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = CustomTheme.colors.backgroundSecondary,
                        unfocusedTextColor = CustomTheme.colors.textPrimary,
                        unfocusedLabelColor = CustomTheme.colors.textSecondary,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = CustomTheme.colors.backgroundSecondary,
                        focusedTextColor = CustomTheme.colors.textPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = CustomTheme.colors.textPrimary,
                        unfocusedPlaceholderColor = CustomTheme.colors.textSecondary,
                        focusedPlaceholderColor = CustomTheme.colors.textSecondary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp)
                )
            }
        }
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(CustomTheme.colors.backgroundPrimary),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = CustomTheme.colors.primary,
                    modifier = Modifier.padding(bottom = 64.dp)
                )
                LinearProgressIndicator(
                    progress = { fetchProgress / 100f },
                    color = CustomTheme.colors.primary,
                    trackColor = CustomTheme.colors.backgroundSecondary,
                    modifier = Modifier.width(200.dp)
                )
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