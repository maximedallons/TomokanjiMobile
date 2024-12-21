package com.gami.tomokanjimobile.ui.composables.words

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.network.WordApi
import com.gami.tomokanjimobile.ui.composables.navigation.BottomNavigationViewModel
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton
import kotlinx.coroutines.launch

@Composable
fun WordDetail(
    id: Int,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    bottomNavigationViewModel: BottomNavigationViewModel,
    viewModel: WordViewModel
) {
    val word = viewModel.fetchWordById(id)
    var mastered by remember { mutableStateOf(viewModel.isMastered(id)) }

    val defaultColor = CustomTheme.colors.backgroundSecondary
    val selectedColor = CustomTheme.colors.primary
    val currentLevel = word.level
    LaunchedEffect(Unit) {
        viewModel.updateCurrentLevel(currentLevel)
        bottomNavigationViewModel.clearCenterButtons()
        for(i in 5 downTo 1) {
            val color = if(i == currentLevel) selectedColor else defaultColor
            bottomNavigationViewModel.addCenterButton(
                CircleButton(
                    label = "N$i",
                    onClick = {
                        viewModel.updateCurrentLevel(i)
                        navController.navigate("word") {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    inclusive = true
                                }
                            }
                            launchSingleTop = true
                        }
                    },
                    background = color
                )
            )
        }
    }

    LaunchedEffect(mastered) {}

    val coroutineScope = rememberCoroutineScope()
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
                .padding(16.dp, 16.dp, 16.dp, 128.dp),
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
                    .padding(16.dp, 16.dp, 16.dp, 24.dp)
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
                            viewModel.updateWordMastery(word.id, mastered)

                            // Update the server in the background
                            coroutineScope.launch {
                                if (mastered) {
                                    WordApi.service.masterWord(sharedViewModel.getUserId(), word.id)
                                } else {
                                    WordApi.service.unmasterWord(sharedViewModel.getUserId(), word.id)
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

                    // Main content inside the card
                    Column(
                        modifier = Modifier.padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Display the kanjis and kanas
                            if (word.kanjis.isNotEmpty()) {
                                Text(
                                    text = word.kanjis[0].text,
                                    fontSize = 24.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                                if (word.kanas.isNotEmpty()) {
                                    Text(
                                        text = word.kanas[0].text,
                                        fontSize = 18.sp,
                                        color = CustomTheme.colors.textSecondary
                                    )
                                }
                            } else if (word.kanas.isNotEmpty()) {
                                Text(
                                    text = word.kanas[0].text,
                                    fontSize = 24.sp,
                                    color = CustomTheme.colors.textPrimary
                                )
                            }
                        }


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Level: N${word.level}",
                                fontSize = 16.sp,
                                color = CustomTheme.colors.textSecondary
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "Meanings:",
                                fontSize = 18.sp,
                                color = CustomTheme.colors.textPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            word.translations.take(3).forEach { meaning ->
                                Text(
                                    text = "• $meaning",
                                    fontSize = 16.sp,
                                    color = CustomTheme.colors.textSecondary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            if(word.examples.isNotEmpty()) {
                                Text(
                                    text = "Examples:",
                                    fontSize = 18.sp,
                                    color = CustomTheme.colors.textPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                                )
                                word.examples.take(2).forEach { example ->
                                    val exampleWord = example.text
                                    val japaneseSentence = example.sentences[0].text
                                    val highlightedSentence = buildAnnotatedString {
                                        val startIndex =
                                            japaneseSentence.indexOf(exampleWord, ignoreCase = true)
                                        if (startIndex >= 0) {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = CustomTheme.colors.textPrimary,
                                                    fontSize = 16.sp
                                                )
                                            ) {
                                                append(japaneseSentence.substring(0, startIndex))
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = CustomTheme.colors.primary,
                                                    fontSize = 16.sp
                                                )
                                            ) { // Highlight style
                                                append(
                                                    japaneseSentence.substring(
                                                        startIndex,
                                                        startIndex + exampleWord.length
                                                    )
                                                )
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = CustomTheme.colors.textPrimary,
                                                    fontSize = 16.sp
                                                )
                                            ) {
                                                append(japaneseSentence.substring(startIndex + exampleWord.length))
                                            }
                                        } else {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = CustomTheme.colors.textPrimary,
                                                    fontSize = 16.sp
                                                )
                                            ) {
                                                append(japaneseSentence) // If not found, keep the original sentence
                                            }
                                        }
                                    }
                                    BasicText(
                                        text = highlightedSentence,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    Text(
                                        text = example.sentences[1].text,
                                        fontSize = 14.sp,
                                        color = CustomTheme.colors.textSecondary,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if(word.kanjis.isNotEmpty()) {
                            val kanjis : MutableList<Kanji> = mutableListOf()
                            val characters = word.kanjis[0].text.toCharArray()
                            for (character in characters) {
                                val kanji = viewModel.fetchKanjiByCharacter(character.toString())
                                if (kanji != null) {
                                    kanjis += kanji
                                }
                            }

                            if(kanjis.isNotEmpty()) {
                                Text(
                                    text = "Related Kanjis:",
                                    fontSize = 18.sp,
                                    color = CustomTheme.colors.textPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp * minOf(kanjis.size, 3)) // Set height to show only 3 items
                                ) {
                                    LazyColumn{
                                        items(kanjis.size) { index ->
                                            val kanji = kanjis[index]
                                            val isKanjiMastered = sharedViewModel.isKanjiMastered(kanji.id)
                                            val wordBorderColor = if (isKanjiMastered) {
                                                CustomTheme.colors.primary
                                            } else {
                                                CustomTheme.colors.backgroundTertiary
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp, 8.dp)
                                                    .height(64.dp) // Set each item's height to 64.dp
                                                    .border(2.dp, wordBorderColor, RoundedCornerShape(16.dp))
                                                    .clickable {
                                                        navController.navigate("kanji_detail/${kanji.id}") {
                                                            navController.graph.startDestinationRoute?.let { route ->
                                                                popUpTo(route) {
                                                                    inclusive = true
                                                                }
                                                            }
                                                            launchSingleTop = true
                                                        }
                                                               },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        modifier = Modifier.padding(16.dp, 4.dp),
                                                        text = kanji.character,
                                                        color = CustomTheme.colors.textPrimary,
                                                        fontSize = 16.sp
                                                    )
                                                    if (kanji.kunyomi.isNotEmpty()) {
                                                        Text(
                                                            modifier = Modifier.padding(16.dp, 4.dp),
                                                            text = kanji.kunyomi[0],
                                                            color = CustomTheme.colors.textSecondary,
                                                            fontSize = 16.sp
                                                        )
                                                    }
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
            }
        }
    }
}