package com.gami.tomokanjimobile.ui.composables.kanas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.network.KanaApi
import com.gami.tomokanjimobile.ui.composables.navigation.BottomNavigationViewModel
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton
import kotlinx.coroutines.launch

@Composable
fun HiraganaDetail(id : Int,
                   navController: NavController,
                   sharedViewModel: SharedViewModel,
                   bottomNavigationViewModel: BottomNavigationViewModel,
                   viewModel: KanaViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val hiragana = viewModel.fetchHiraganaById(id)
    var mastered by remember { mutableStateOf(viewModel.isHiraganaMastered(id)) }
    val currentType by viewModel.currentType.collectAsState()

    LaunchedEffect(mastered) {}

    val defaultColor = CustomTheme.colors.backgroundSecondary
    val selectedColor = CustomTheme.colors.primary
    LaunchedEffect(currentType) {
        bottomNavigationViewModel.clearCenterButtons()
        bottomNavigationViewModel.addCenterButton(
            CircleButton(
                label = "Hiragana",
                onClick = {
                    viewModel.updateCurrentType("H")
                    navController.navigate("kana") {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                inclusive = true
                            }
                        }
                        launchSingleTop = true
                    }
                },
                background = if(currentType == "H") selectedColor else defaultColor
            )
        )
        bottomNavigationViewModel.addCenterButton(
            CircleButton(
                label = "Katakana",
                onClick = {
                    viewModel.updateCurrentType("K")
                    navController.navigate("kana") {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                inclusive = true
                            }
                        }
                        launchSingleTop = true
                    }
                },
                background = if(currentType == "K") selectedColor else defaultColor
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val borderColor = if (mastered) {
                CustomTheme.colors.primary
            } else {
                CustomTheme.colors.backgroundSecondary
            }

            // Card wrapper with new buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.backgroundSecondary)
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back button in the top-left
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CustomTheme.colors.textPrimary
                        )
                    }

                    // Master button in the top-right
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        onClick = {
                            mastered = !mastered
                            viewModel.updateHiraganaMastery(id, mastered)

                            // Update the server in the background
                            coroutineScope.launch {
                                if (mastered) {
                                    KanaApi.service.masterHiragana(1, hiragana.id)
                                } else {
                                    KanaApi.service.unmasterHiragana(1, hiragana.id)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = if (mastered) "Unmaster" else "Master",
                            tint = if (mastered) CustomTheme.colors.primary else CustomTheme.colors.textPrimary
                        )
                    }

                    Column(
                        modifier = Modifier.padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
                        {
                            Text(
                                text = hiragana.kana,
                                fontSize = 48.sp,
                                color = CustomTheme.colors.textPrimary
                            )

                            Text(
                                text = hiragana.romaji,
                                fontSize = 24.sp,
                                color = CustomTheme.colors.textSecondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        val matchingKatakana = sharedViewModel.katakanas.value.filter {
                            it.first.romaji == hiragana.romaji
                        }.first()
                        val isKatakanaMastered = matchingKatakana.second
                        val katakanaBorderColor = if (isKatakanaMastered) {
                            CustomTheme.colors.primary
                        } else {
                            CustomTheme.colors.backgroundTertiary
                        }

                        Text(
                            text = "Katakana counterpart",
                            fontSize = 16.sp,
                            color = CustomTheme.colors.textPrimary,
                            modifier = Modifier.padding(16.dp, 32.dp, 16.dp, 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp, 8.dp)
                                    .height(64.dp) // Set each item's height to 64.dp
                                    .border(2.dp, katakanaBorderColor, RoundedCornerShape(16.dp))
                                    .clickable {
                                        navController.navigate("kana_katakana_detail/${matchingKatakana.first.id}") {
                                            navController.graph.startDestinationRoute?.let { route ->
                                                popUpTo(route) {
                                                    inclusive = true
                                                }
                                            }
                                            launchSingleTop = true
                                        }
                                        viewModel.updateCurrentType("K")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        modifier = Modifier.padding(16.dp, 4.dp),
                                        text = matchingKatakana.first.kana,
                                        color = CustomTheme.colors.textPrimary,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        modifier = Modifier.padding(16.dp, 4.dp),
                                        text = matchingKatakana.first.romaji,
                                        color = CustomTheme.colors.textSecondary,
                                        fontSize = 16.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = CustomTheme.colors.textPrimary,
                                    modifier = Modifier
                                        .padding(end = 16.dp) // Add padding on the right
                                        .align(Alignment.CenterVertically) // For vertical centering
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}