package com.gami.tomokanjimobile.ui.composables.kanjis

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
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.network.KanjiApi
import com.gami.tomokanjimobile.ui.composables.LevelSelectionHeader
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun KanjiScreen(viewModel: KanjiViewModel = viewModel(), navController: NavController, context: Context) {
    val kanjiDatabase = KanjiDatabaseBuilder.getInstance(context)
    val kanjiDao = kanjiDatabase.kanjiDao()
    val coroutineScope = rememberCoroutineScope()
    val showKunyomi by viewModel.showKunyomi.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var kanjis by remember { mutableStateOf(emptyList<Kanji>()) }
    var masteredKanjiIds by remember { mutableStateOf(emptyList<Int>()) }
    var formattedKanjis by remember { mutableStateOf(emptyList<Pair<Kanji, Boolean>>()) }

    fun updateFormattedKanjis(kanjiList: List<Kanji>) {
        formattedKanjis = kanjiList.map { kanji ->
            Pair(kanji, masteredKanjiIds.contains(kanji.id))
        }
    }

    LaunchedEffect(currentLevel) {
        // Set loading and fetching data when the refresh flag changes
        println("Refreshing Kanjis...") // For debugging purposes
        isLoading = true
        kanjis = kanjiDao.getKanjisForLevel(currentLevel)
        if (kanjis.isEmpty()) {
            kanjis = KanjiApi.service.getKanjisForLevel(currentLevel)
            kanjiDao.insertAll(kanjis)
        }

        coroutineScope.launch {
            masteredKanjiIds = KanjiApi.service.getMasteredKanjiIds(1)
            updateFormattedKanjis(kanjis)
            println("Mastered Kanji: $masteredKanjiIds") // For debugging purposes
            isLoading = false
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
                formattedKanjis,
                showKunyomi,
                onShowKunyomiChange = { viewModel.toggleShowKunyomi() } ,
                navController,
                coroutineScope)
        }
    }
}