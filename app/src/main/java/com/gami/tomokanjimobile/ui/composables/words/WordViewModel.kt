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

    private val _filteredWords = MutableStateFlow<List<Pair<Word, Boolean>>>(emptyList())
    val filteredWords: StateFlow<List<Pair<Word, Boolean>>> get() = _filteredWords

    private val _fetchProgress = MutableStateFlow(0)
    val fetchProgress: StateFlow<Int> get() = _fetchProgress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _showKanas = MutableStateFlow(true)
    val showKanas: StateFlow<Boolean> get() = _showKanas

    private val _showTranslations = MutableStateFlow(true)
    val showTranslations: StateFlow<Boolean> get() = _showTranslations

    private val _currentLevel = MutableStateFlow(5)
    val currentLevel: StateFlow<Int> = _currentLevel

    private var masteredWordIds = emptyList<Int>()

    private var _query = MutableStateFlow("")
    val query : StateFlow<String> get() = _query

    fun toggleShowKanas() {
        _showKanas.value = !_showKanas.value
    }

    fun toggleShowTranslations() {
        _showTranslations.value = !_showTranslations.value
    }

    fun updateCurrentLevel(level: Int) {
        _currentLevel.update { level }
    }

    fun updateWordMastery(wordId: Int, isMastered: Boolean) {
        _words.update { wordList ->
            wordList.map { if (it.first.id == wordId) Pair(it.first, isMastered) else it }
        }
    }

    fun fetchWords(wordDao: WordDao) {
        viewModelScope.launch {
            _isLoading.value = true

            val kanjiCount = wordDao.getWordCount()
            masteredWordIds = WordApi.service.getMasteredWordIds(1)

            if(kanjiCount > 0) {
                val chunkSize = (kanjiCount + 99) / 100
                val allWords = mutableListOf<Pair<Word, Boolean>>()

                for (i in 0 until 100) {
                    val tempWords = wordDao.getWordsChunk(chunkSize, i * chunkSize)
                    _fetchProgress.value += 1

                    allWords.addAll(tempWords.map { word ->
                        Pair(word, masteredWordIds.contains(word.id))
                    })
                }

                _words.value = allWords // Assign the accumulated list of kanjis
            }
            else {
                val tempWords = WordApi.service.getWords()
                wordDao.insertAll(tempWords)
                _words.value = tempWords.map { word ->
                    Pair(word, masteredWordIds.contains(word.id))
                }
            }

            fetchWordsForLevel(5)

            _isLoading.value = false
        }
    }

    fun fetchWordsForLevel(level: Int) {
        _filteredWords.value = _words.value.filter { it.first.level == level }
    }

    fun filterWordsIds(query: String) {
        _query.value = query

        if(_query.value.isEmpty()) {
            fetchWordsForLevel(_currentLevel.value)
            return
        }

        _filteredWords.value = _words.value.filter {
            it.first.kanjis.any { kanji ->
                kanji.text.contains(query, ignoreCase = true)
            } ||
            it.first.kanas.any { kanas ->
                kanas.text.contains(query, ignoreCase = true)
            } ||
            it.first.translations.any { translation ->
                translation.contains(query, ignoreCase = true)
            }
        }
    }
}