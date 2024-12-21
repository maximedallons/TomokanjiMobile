package com.gami.tomokanjimobile

import androidx.lifecycle.ViewModel
import com.gami.tomokanjimobile.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel() : ViewModel() {
    private val _loggedUser = MutableStateFlow<User?>(null)

    private val _kanjis = MutableStateFlow<List<Pair<Kanji, Boolean>>>(emptyList())
    val kanjis: StateFlow<List<Pair<Kanji, Boolean>>> get() = _kanjis

    private val _words = MutableStateFlow<List<Pair<Word, Boolean>>>(emptyList())
    val words: StateFlow<List<Pair<Word, Boolean>>> get() = _words

    private val _hiraganas = MutableStateFlow<List<Pair<Hiragana, Boolean>>>(emptyList())
    val hiraganas: StateFlow<List<Pair<Hiragana, Boolean>>> get() = _hiraganas

    private val _katakanas = MutableStateFlow<List<Pair<Katakana, Boolean>>>(emptyList())
    val katakanas: StateFlow<List<Pair<Katakana, Boolean>>> get() = _katakanas

    fun updateKanjis(kanjis: List<Pair<Kanji, Boolean>>) {
        _kanjis.value = kanjis
    }

    fun updateWords(words: List<Pair<Word, Boolean>>) {
        _words.value = words
    }

    fun updateHiraganas(hiraganas: List<Pair<Hiragana, Boolean>>) {
        _hiraganas.value = hiraganas
    }

    fun updateKatakanas(katakanas: List<Pair<Katakana, Boolean>>) {
        _katakanas.value = katakanas
    }

    fun isKanjiMastered(id: Int) : Boolean {
        return _kanjis.value.find { it.first.id == id }?.second ?: false
    }

    fun isWordMastered(id: Int) : Boolean {
        return _words.value.find { it.first.id == id }?.second ?: false
    }

    fun isHiraganaMastered(id: Int) : Boolean {
        return _hiraganas.value.find { it.first.id == id }?.second ?: false
    }

    fun isKatakanaMastered(id: Int) : Boolean {
        return _katakanas.value.find { it.first.id == id }?.second ?: false
    }

    fun setLoggedUser(user: User) {
        _loggedUser.value = user
    }

    fun getLoggedUser(): User? {
        return _loggedUser.value
    }

    fun getUserId(): Int {
        return _loggedUser.value?.user_id ?: -1
    }

    fun getUserLogin(): String {
        return _loggedUser.value?.login ?: ""
    }

    fun getUserCookie(): String {
        return _loggedUser.value?.cookie ?: ""
    }
}