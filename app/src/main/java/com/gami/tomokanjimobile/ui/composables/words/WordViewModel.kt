package com.gami.tomokanjimobile.ui.composables.words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.dao.WordDao
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.network.WordApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WordViewModelFactory(private val sharedViewModel: SharedViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            return WordViewModel(sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WordViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {
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

            val wordCount = wordDao.getWordCount()

            if (wordCount > 0) {
                val chunkSize = (wordCount + 99) / 100
                val allWords = mutableListOf<Pair<Word, Boolean>>()

                for (i in 0 until 100) {
                    val tempWords = wordDao.getWordsChunk(chunkSize, i * chunkSize)
                    _fetchProgress.value += 1

                    allWords.addAll(tempWords.map { word ->
                        Pair(word, false) // Default the mastery to false initially
                    })
                }

                _words.value = allWords // Assign the accumulated list of words
            } else {
                val tempWords = WordApi.service.getWords()
                wordDao.insertAll(tempWords)
                _words.value = tempWords.map { word ->
                    Pair(word, false) // Default the mastery to false initially
                }
            }

            if(sharedViewModel.getLoggedUser() != null) {
                println("Fetching mastered words")
                fetchUserMasteredWords()
            }

            _isLoading.value = false
        }
    }

    // Fetch the mastered words after login
    fun fetchUserMasteredWords() {
        viewModelScope.launch {
            masteredWordIds = WordApi.service.getMasteredWordIds(sharedViewModel.getUserId())

            _words.update { wordList ->
                wordList.map { (word, _) ->
                    Pair(word, masteredWordIds.contains(word.id)) // Update mastery status
                }
            }

            fetchWordsForLevel(_currentLevel.value)
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