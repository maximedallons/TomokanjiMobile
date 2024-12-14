package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.lifecycle.viewModelScope
import com.gami.tomokanjimobile.SearchableViewModel
import com.gami.tomokanjimobile.dao.KanjiDao
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KanjiViewModel : SearchableViewModel() {
    private val _kanjis = MutableStateFlow<List<Pair<Kanji, Boolean>>>(emptyList())
    val kanjis: StateFlow<List<Pair<Kanji, Boolean>>> get() = _kanjis

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _showKunyomi = MutableStateFlow(true)
    val showKunyomi: StateFlow<Boolean> get() = _showKunyomi

    private val _currentLevel = MutableStateFlow(5)
    val currentLevel: StateFlow<Int> = _currentLevel

    private val _needsUpdate = MutableStateFlow(true)
    val needsUpdate: StateFlow<Boolean> = _needsUpdate

    private var masteredKanjiIds = emptyList<Int>()

    fun toggleShowKunyomi() {
        _showKunyomi.value = !_showKunyomi.value
    }

    fun updateCurrentLevel(level: Int) {
        _currentLevel.update { level }
        updateNeedsUpdate(true)
    }

    fun updateKanjiMastery(kanjiId: Int, isMastered: Boolean) {
        _kanjis.update { kanjiList ->
            kanjiList.map { if (it.first.id == kanjiId) Pair(it.first, isMastered) else it }
        }
    }

    fun updateNeedsUpdate(doUpdate : Boolean) {
        _needsUpdate.update { doUpdate }
    }

    fun fetchKanjisForLevel(kanjiDao: KanjiDao, level: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            var kanjis = kanjiDao.getKanjisForLevel(level)

            if (kanjis.isEmpty()) {
                kanjis = KanjiApi.service.getKanjisForLevel(level)
                kanjiDao.insertAll(kanjis)
            }

            masteredKanjiIds = KanjiApi.service.getMasteredKanjiIds(1)

            _kanjis.value = kanjis.map { kanji ->
                Pair(kanji, masteredKanjiIds.contains(kanji.id))
            }

            _isLoading.value = false
        }
    }
}