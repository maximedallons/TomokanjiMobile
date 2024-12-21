package com.gami.tomokanjimobile.ui.composables.kanas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.dao.HiraganaDao
import com.gami.tomokanjimobile.dao.KanjiDao
import com.gami.tomokanjimobile.dao.KatakanaDao
import com.gami.tomokanjimobile.data.Hiragana
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Katakana
import com.gami.tomokanjimobile.network.KanaApi
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KanaViewModelFactory(private val sharedViewModel: SharedViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanaViewModel::class.java)) {
            return KanaViewModel(sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class KanaViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {
    private val _filteredHiraganas = MutableStateFlow<List<Pair<Hiragana, Boolean>>>(emptyList())
    val filteredHiraganas: StateFlow<List<Pair<Hiragana, Boolean>>> get() = _filteredHiraganas
    
    private val _filteredKatakanas = MutableStateFlow<List<Pair<Katakana, Boolean>>>(emptyList())
    val filteredKatakanas: StateFlow<List<Pair<Katakana, Boolean>>> get() = _filteredKatakanas

    private val _fetchProgress = MutableStateFlow(0)
    val fetchProgress: StateFlow<Int> get() = _fetchProgress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    private val _currentType = MutableStateFlow("H")
    val currentType: StateFlow<String> get() = _currentType
    
    private var masteredHiraganaIds = emptyList<Int>()
    private var masteredKatakanaIds = emptyList<Int>()

    private var _query = MutableStateFlow("")
    val query : StateFlow<String> get() = _query
    
    fun updateCurrentType(type: String) {
        _currentType.value = type
    }
    
    fun filterKanas(query: String) {
        _query.value = query
        _filteredHiraganas.value = sharedViewModel.hiraganas.value.filter { (hiragana, _) ->
            hiragana.kana.contains(query, ignoreCase = true) || hiragana.romaji.contains(query, ignoreCase = true)
        }
        _filteredKatakanas.value = sharedViewModel.katakanas.value.filter { (katakana, _) ->
            katakana.kana.contains(query, ignoreCase = true) || katakana.romaji.contains(query, ignoreCase = true)
        }
    }
    
    fun updateHiraganaMastery(hiraganaId: Int, isMastered: Boolean) {
        sharedViewModel.updateHiraganas(
            sharedViewModel.hiraganas.value.map { (hiragana, mastered) ->
                if (hiragana.id == hiraganaId) Pair(hiragana, isMastered) else Pair(hiragana, mastered)
            }
        )
    }
    
    fun updateKatakanaMastery(katakanaId: Int, isMastered: Boolean) {
        sharedViewModel.updateKatakanas(
            sharedViewModel.katakanas.value.map { (katakana, mastered) ->
                if (katakana.id == katakanaId) Pair(katakana, isMastered) else Pair(katakana, mastered)
            }
        )
    }

    fun fetchHiraganas(hiraganaDao: HiraganaDao) {
        viewModelScope.launch {
            _isLoading.value = true

            val hiraganaCount = hiraganaDao.getHiraganaCount()

            if (hiraganaCount > 0) {
                val chunkSize = (hiraganaCount + 19) / 20
                val allHiraganas = mutableListOf<Pair<Hiragana, Boolean>>()

                for (i in 0 until 20) {
                    val tempHiraganas = hiraganaDao.getHiraganasChunk(chunkSize, i * chunkSize)
                    _fetchProgress.value += 1

                    allHiraganas.addAll(tempHiraganas.map { hiragana ->
                        Pair(hiragana, false) // Default the mastery to false initially
                    })
                }

                sharedViewModel.updateHiraganas(allHiraganas)
            } else {
                val tempHiraganas = KanaApi.service.getHiraganas()
                hiraganaDao.insertAll(tempHiraganas)
                sharedViewModel.updateHiraganas(tempHiraganas.map { hiragana ->
                    Pair(hiragana, false) // Default the mastery to false initially
                })
            }

            if(sharedViewModel.getLoggedUser() != null) {
                fetchUserMasteredHiraganas()
            }
        }
    }

    fun fetchUserMasteredHiraganas() {
        viewModelScope.launch {
            masteredHiraganaIds = KanaApi.service.getMasteredHiraganaIds(sharedViewModel.getUserId())

            sharedViewModel.updateHiraganas(
                sharedViewModel.hiraganas.value.map { (hiragana, _) ->
                    Pair(hiragana, masteredHiraganaIds.contains(hiragana.id)) // Update mastery status
                }
            )

            resetFilterHiraganaFilter()

            _isLoading.value = false
        }
    }
    
    fun resetFilterHiraganaFilter() {
        _query.value = ""
        _filteredHiraganas.value = sharedViewModel.hiraganas.value
    }

    fun fetchHiraganaById(id: Int) : Hiragana {
        return sharedViewModel.hiraganas.value.first { it.first.id == id }.first
    }

    fun isHiraganaMastered(id: Int) : Boolean {
        return sharedViewModel.hiraganas.value.first { it.first.id == id }.second
    }

    fun filterHiraganasIds(query: String) {
        _query.value = query

        if(_query.value.isEmpty()) {
            resetFilterHiraganaFilter()
            return
        }

        _filteredHiraganas.value = sharedViewModel.hiraganas.value.filter {
            it.first.kana.contains(query, ignoreCase = true) ||
            it.first.romaji.contains(query, ignoreCase = true)
        }
    }

    fun fetchKatakanas(katakanaDao: KatakanaDao) {
        viewModelScope.launch {
            _isLoading.value = true

            val katakanaCount = katakanaDao.getKatakanaCount()

            if (katakanaCount > 0) {
                val chunkSize = (katakanaCount + 19) / 20
                val allKatakanas = mutableListOf<Pair<Katakana, Boolean>>()

                for (i in 0 until 20) {
                    val tempKatakanas = katakanaDao.getKatakanasChunk(chunkSize, i * chunkSize)
                    _fetchProgress.value += 1

                    allKatakanas.addAll(tempKatakanas.map { katakana ->
                        Pair(katakana, false) // Default the mastery to false initially
                    })
                }

                sharedViewModel.updateKatakanas(allKatakanas)
            } else {
                val tempKatakanas = KanaApi.service.getKatakanas()
                katakanaDao.insertAll(tempKatakanas)
                sharedViewModel.updateKatakanas(tempKatakanas.map { katakana ->
                    Pair(katakana, false) // Default the mastery to false initially
                })
            }

            if(sharedViewModel.getLoggedUser() != null) {
                fetchUserMasteredKatakanas()
            }
        }
    }

    fun fetchUserMasteredKatakanas() {
        viewModelScope.launch {
            masteredKatakanaIds = KanaApi.service.getMasteredKatakanaIds(sharedViewModel.getUserId())

            sharedViewModel.updateKatakanas(
                sharedViewModel.katakanas.value.map { (katakana, _) ->
                    Pair(katakana, masteredKatakanaIds.contains(katakana.id)) // Update mastery status
                }
            )

            resetFilterKatakanaFilter()

            _isLoading.value = false
        }
    }

    fun resetFilterKatakanaFilter() {
        _query.value = ""
        _filteredKatakanas.value = sharedViewModel.katakanas.value
    }

    fun fetchKatakanaById(id: Int) : Katakana {
        return sharedViewModel.katakanas.value.first { it.first.id == id }.first
    }

    fun isKatakanaMastered(id: Int) : Boolean {
        return sharedViewModel.katakanas.value.first { it.first.id == id }.second
    }

    fun filterKatakanasIds(query: String) {
        _query.value = query

        if(_query.value.isEmpty()) {
            resetFilterKatakanaFilter()
            return
        }

        _filteredKatakanas.value = sharedViewModel.katakanas.value.filter {
            it.first.kana.contains(query, ignoreCase = true) ||
                    it.first.romaji.contains(query, ignoreCase = true)
        }
    }
}