package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.dao.KanjiDao
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KanjiViewModelFactory(private val sharedViewModel: SharedViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanjiViewModel::class.java)) {
            return KanjiViewModel(sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class KanjiViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {
    private val _filteredKanjis = MutableStateFlow<List<Pair<Kanji, Boolean>>>(emptyList())
    val filteredKanjis: StateFlow<List<Pair<Kanji, Boolean>>> get() = _filteredKanjis

    private val _fetchProgress = MutableStateFlow(0)
    val fetchProgress: StateFlow<Int> get() = _fetchProgress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _showKunyomi = MutableStateFlow(true)
    val showKunyomi: StateFlow<Boolean> get() = _showKunyomi

    private val _currentLevel = MutableStateFlow(5)
    val currentLevel: StateFlow<Int> = _currentLevel

    private var masteredKanjiIds = emptyList<Int>()

    private var _query = MutableStateFlow("")
    val query : StateFlow<String> get() = _query

    fun toggleShowKunyomi() {
        _showKunyomi.value = !_showKunyomi.value
    }

    fun updateCurrentLevel(level: Int) {
        _currentLevel.update { level }
    }

    fun updateKanjiMastery(kanjiId: Int, isMastered: Boolean) {
        sharedViewModel.updateKanjis(
            sharedViewModel.kanjis.value.map { (kanji, mastered) ->
                if (kanji.id == kanjiId) Pair(kanji, isMastered) else Pair(kanji, mastered)
            }
        )
    }

    fun fetchKanjis(kanjiDao: KanjiDao) {
        viewModelScope.launch {
            _isLoading.value = true

            val kanjiCount = kanjiDao.getKanjiCount()

            if (kanjiCount > 0) {
                val chunkSize = (kanjiCount + 99) / 100
                val allKanjis = mutableListOf<Pair<Kanji, Boolean>>()

                for (i in 0 until 100) {
                    val tempKanjis = kanjiDao.getKanjisChunk(chunkSize, i * chunkSize)
                    _fetchProgress.value += 1

                    allKanjis.addAll(tempKanjis.map { kanji ->
                        Pair(kanji, false) // Default the mastery to false initially
                    })
                }

                sharedViewModel.updateKanjis(allKanjis)
            } else {
                val tempKanjis = KanjiApi.service.getKanjis()
                kanjiDao.insertAll(tempKanjis)
                sharedViewModel.updateKanjis(tempKanjis.map { kanji ->
                    Pair(kanji, false) // Default the mastery to false initially
                })
            }

            if(sharedViewModel.getLoggedUser() != null) {
                println("Fetching mastered kanjis")
                fetchUserMasteredKanjis()
            }

            _isLoading.value = false
        }
    }

    // Fetch the mastered kanjis after login
    fun fetchUserMasteredKanjis() {
        viewModelScope.launch {
            masteredKanjiIds = KanjiApi.service.getMasteredKanjiIds(sharedViewModel.getUserId())

            sharedViewModel.updateKanjis(
                sharedViewModel.kanjis.value.map { (kanji, _) ->
                    Pair(kanji, masteredKanjiIds.contains(kanji.id)) // Update mastery status
                }
            )

            fetchKanjisForLevel(_currentLevel.value)
        }
    }

    fun fetchKanjiById(id: Int) : Kanji {
        return sharedViewModel.kanjis.value.first { it.first.id == id }.first
    }

    fun isMastered(id: Int) : Boolean {
        return sharedViewModel.kanjis.value.first { it.first.id == id }.second
    }

    fun fetchKanjisForLevel(level: Int) {
        _filteredKanjis.value = sharedViewModel.kanjis.value.filter { it.first.level == level }
    }

    fun filterKanjisIds(query: String) {
        _query.value = query

        if(_query.value.isEmpty()) {
            fetchKanjisForLevel(_currentLevel.value)
            return
        }

        _filteredKanjis.value = sharedViewModel.kanjis.value.filter {
            it.first.character.contains(query, ignoreCase = true) ||
            it.first.meanings.any { meaning ->
                meaning.contains(query, ignoreCase = true)
            } ||
            it.first.onyomi.any { onyomi ->
                onyomi.contains(query, ignoreCase = true)
            } ||
            it.first.kunyomi.any { kunyomi ->
                kunyomi.contains(query, ignoreCase = true)
            }
        }
    }

    fun fetchWordsByKanjiText(kanjiText: String) : List<Word> {
        return sharedViewModel.words.value.filter { it.first.kanjis.any { kanji -> kanji.text.contains(kanjiText, ignoreCase = true) } }
            .map { it.first }
    }
}