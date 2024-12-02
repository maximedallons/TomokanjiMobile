package com.gami.tomokanjimobile.ui.composables.kanjis

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.dao.KanjiDatabaseBuilder
import com.gami.tomokanjimobile.dao.MasteredKanjiDatabaseBuilder
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.MasteredKanji
import com.gami.tomokanjimobile.network.KanjiApi
import com.gami.tomokanjimobile.ui.composables.LevelSelectionHeader
import kotlinx.coroutines.launch

@Composable
fun KanjiScreen(context: Context) {
    val kanjiDatabase = KanjiDatabaseBuilder.getInstance(context)
    val masteredKanjiDatabase = MasteredKanjiDatabaseBuilder.getInstance(context)
    val kanjiDao = kanjiDatabase.kanjiDao()
    val masteredKanjiDao = masteredKanjiDatabase.masteredKanjiDao()
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var currentLevel by remember { mutableStateOf(5) }
    var kanjis by remember { mutableStateOf(emptyList<Kanji>()) }
    var masteredKanjis by remember { mutableStateOf(emptyList<MasteredKanji>()) }
    var formattedKanjis by remember { mutableStateOf(emptyList<Pair<Kanji, Boolean>>()) }

    fun updateFormattedKanjis(kanjiList: List<Kanji>) {
        formattedKanjis = kanjiList.map { kanji ->
            Pair(kanji, masteredKanjis.any { masteredKanji -> masteredKanji.id == kanji.id })
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            kanjis = kanjiDao.getKanjisForLevel(currentLevel)
            if (kanjis.isEmpty()) {
                kanjis = KanjiApi.service.getKanjisForLevel(currentLevel)
                kanjiDao.insertAll(kanjis)
            }
            masteredKanjis = masteredKanjiDao.getAllKanjis()
            if (masteredKanjis.isEmpty()) {
                masteredKanjis = KanjiApi.service.getUserKanjis(1)
                masteredKanjiDao.insertAll(masteredKanjis)
            }
            updateFormattedKanjis(kanjis)
            isLoading = false
        }
    }

    Column {
        LevelSelectionHeader(currentLevel) { selectedLevel ->
            currentLevel = selectedLevel
            coroutineScope.launch {
                isLoading = true
                kanjis = kanjiDao.getKanjisForLevel(selectedLevel)
                updateFormattedKanjis(kanjis)
                isLoading = false
            }
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
            KanjiList(formattedKanjis, currentLevel, coroutineScope, context)
        }
    }
}