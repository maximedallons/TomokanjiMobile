package com.gami.tomokanjimobile.ui.composables.kanjis

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.dao.MasteredKanjiDatabaseBuilder
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun KanjiList(kanjis: List<Pair<Kanji, Boolean>>, level: Int, coroutineScope: CoroutineScope, context: Context) {
    var selectedKanji by remember { mutableStateOf<Pair<Kanji, Boolean>?>(null) }
    val listState = rememberLazyGridState()
    var mutableKanjis by remember { mutableStateOf(kanjis.toMutableList()) }
    var showKunyomi by remember { mutableStateOf(true) }

    if (selectedKanji == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(CustomTheme.colors.backgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                state = listState
            ) {
                val kanjisByLevel = mutableKanjis.filter { it.first.level == level }
                items(kanjisByLevel.size) { index ->
                    val (kanji, mastered) = kanjisByLevel[index]
                    KanjiCard(
                        kanji = kanji,
                        mastered = mastered,
                        onClick = { selectedKanji = kanjisByLevel[index] },
                        showKunyomi = showKunyomi
                    )
                }
            }
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = CustomTheme.colors.secondary,
                contentColor = CustomTheme.colors.textPrimary
            ) {
                Icon(Icons.Filled.Home, contentDescription = "Scroll to Top")
            }

            FloatingActionButton(
                onClick = { showKunyomi = !showKunyomi },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                containerColor = CustomTheme.colors.secondary,
                contentColor = CustomTheme.colors.textPrimary
            ) {
                Icon(Icons.Filled.Done, contentDescription = "Toggle Kunyomi")
            }
        }
    } else {
        KanjiDetail(
            kanji = selectedKanji!!.first,
            isMastered = selectedKanji!!.second,
            onBack = {
                selectedKanji = null
                coroutineScope.launch {
                    val latestMasteredKanjis = KanjiApi.service.getUserKanjis(1)
                    mutableKanjis = mutableKanjis.map { (kanji, _) ->
                        val mastered = latestMasteredKanjis.any { it.id == kanji.id }
                        Pair(kanji, mastered)
                    }.toMutableList()
                }
            },
            onToggleMastered = { isMastered ->
                coroutineScope.launch {
                    if (isMastered)
                        KanjiApi.service.masterKanji(1, selectedKanji!!.first.id)
                    else
                        KanjiApi.service.unmasterKanji(1, selectedKanji!!.first.id)
                    updateMasteredKanjiDb(context)
                }
                selectedKanji = selectedKanji!!.copy(second = isMastered)
            }
        )
    }
}

private suspend fun updateMasteredKanjiDb(context: Context) {
    val masteredKanjiDatabase = MasteredKanjiDatabaseBuilder.getInstance(context)
    val masteredKanjiDao = masteredKanjiDatabase.masteredKanjiDao()
    val latestMasteredKanjis = KanjiApi.service.getUserKanjis(1)

    withContext(Dispatchers.IO) {
        masteredKanjiDao.deleteAll()
        masteredKanjiDao.insertAll(latestMasteredKanjis)
    }
}