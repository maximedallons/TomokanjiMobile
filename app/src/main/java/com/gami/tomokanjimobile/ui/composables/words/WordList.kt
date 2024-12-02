package com.gami.tomokanjimobile.ui.composables.words

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun WordList(
    viewModel: WordViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val words by viewModel.words.collectAsState()
    val showKanas by viewModel.showKanas.collectAsState()
    val showTranslations by viewModel.showTranslations.collectAsState()
    val listState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = listState
        ) {
            items(words.size) { index ->
                val (word, mastered) = words[index]
                WordCard(
                    word = word,
                    mastered = mastered,
                    onClick = {
                        navController.navigate("word_detail/${Json.encodeToString(word)}/${mastered}")
                    },
                    showKanas = showKanas,
                    showTranslations = showTranslations
                )
            }
        }
//        FloatingActionButton(
//            onClick = {
//                coroutineScope.launch {
//                    listState.animateScrollToItem(0)
//                }
//            },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp),
//            containerColor = CustomTheme.colors.secondary,
//            contentColor = CustomTheme.colors.textPrimary
//        ) {
//            Icon(
//                modifier = Modifier.size(24.dp),
//                painter = painterResource(id = R.drawable.arrow_up),
//                tint = CustomTheme.colors.textPrimary,
//                contentDescription = "Scroll to top"
//            )
//        }
    }
}