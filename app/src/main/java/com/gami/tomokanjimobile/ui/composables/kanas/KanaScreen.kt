package com.gami.tomokanjimobile.ui.composables.kanas

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiList
import com.gami.tomokanjimobile.ui.composables.navigation.BottomNavigationViewModel
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton

@Composable
fun KanaScreen(
    viewModel: KanaViewModel = viewModel(),
    bottomNavigationViewModel: BottomNavigationViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()

    val currentType by viewModel.currentType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val fetchProgress by viewModel.fetchProgress.collectAsState()

    val query by viewModel.query.collectAsState()

    val defaultColor = CustomTheme.colors.backgroundSecondary
    val selectedColor = CustomTheme.colors.primary

    LaunchedEffect(currentType) {
        bottomNavigationViewModel.clearCenterButtons()
        bottomNavigationViewModel.addCenterButton(
            CircleButton(
                label = "Hiragana",
                onClick = {
                    viewModel.updateCurrentType("H")
                },
                background = if(currentType == "H") selectedColor else defaultColor
            )
        )
        bottomNavigationViewModel.addCenterButton(
            CircleButton(
                label = "Katakana",
                onClick = {
                    viewModel.updateCurrentType("K")
                },
                background = if(currentType == "K") selectedColor else defaultColor
            )
        )

        viewModel.filterKanas(viewModel.query.value)
    }

    Column {
        Row(
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
                val text = if(currentType == "H") "Hiraganas" else "Katakanas"
                Text(
                    text = text,
                    fontSize = 24.sp,
                    color = CustomTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(top = 60.dp, start = 16.dp, bottom = 16.dp)
                )
                if (!isLoading) {
                    TextField(
                        value = query,
                        onValueChange = {
                            if (currentType == "H") viewModel.filterHiraganasIds(it) else viewModel.filterKatakanasIds(
                                it
                            )
                        },
                        placeholder = { if (currentType == "H") Text("Search for a Hiragana") else Text("Search for a Katakana") }, // Use placeholder instead of label
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
        }

        if(isLoading) {
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
            KanaList(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}