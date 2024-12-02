package com.gami.tomokanjimobile.ui.composables.words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gami.tomokanjimobile.dao.WordDao
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.network.WordApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WordViewModel : ViewModel() {
    private val _words = MutableStateFlow<List<Pair<Word, Boolean>>>(emptyList())
    val words: StateFlow<List<Pair<Word, Boolean>>> get() = _words

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _showKanas = MutableStateFlow(true)
    val showKanas: StateFlow<Boolean> get() = _showKanas

    private val _showTranslations = MutableStateFlow(true)
    val showTranslations: StateFlow<Boolean> get() = _showTranslations

    private val _currentLevel = MutableStateFlow(5)
    val currentLevel: StateFlow<Int> = _currentLevel

    private val _needsUpdate = MutableStateFlow(true)
    val needsUpdate: StateFlow<Boolean> = _needsUpdate

    private var masteredWordIds = emptyList<Int>()

    fun toggleShowKanas() {
        _showKanas.value = !_showKanas.value
    }

    fun toggleShowTranslations() {
        _showTranslations.value = !_showTranslations.value
    }

    fun updateCurrentLevel(level: Int) {
        _currentLevel.update { level }
        updateNeedsUpdate(true)
    }

    fun updateNeedsUpdate(doUpdate : Boolean) {
        _needsUpdate.update { doUpdate }
    }

    fun updateWordMastery(wordId: Int, isMastered: Boolean) {
        _words.update { wordList ->
            wordList.map { if (it.first.id == wordId) Pair(it.first, isMastered) else it }
        }
    }

    fun fetchWordsForLevel(wordDao: WordDao, level: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            var words = wordDao.getWordsForLevel(level)

            if (words.isEmpty()) {
                words = WordApi.service.getWordsForLevel(level)
                wordDao.insertAll(words)
            }

            masteredWordIds = WordApi.service.getMasteredWordIds(1)

            _words.value = words.map { word ->
                Pair(word, masteredWordIds.contains(word.id))
            }

            _isLoading.value = false
        }
    }
}