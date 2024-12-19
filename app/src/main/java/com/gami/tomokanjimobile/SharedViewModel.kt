package com.gami.tomokanjimobile

import androidx.lifecycle.ViewModel
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.User
import com.gami.tomokanjimobile.data.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel() : ViewModel() {
    private val _loggedUser = MutableStateFlow<User?>(null)

    private val _kanjis = MutableStateFlow<List<Pair<Kanji, Boolean>>>(emptyList())
    val kanjis: StateFlow<List<Pair<Kanji, Boolean>>> get() = _kanjis

    private val _words = MutableStateFlow<List<Pair<Word, Boolean>>>(emptyList())
    val words: StateFlow<List<Pair<Word, Boolean>>> get() = _words

    fun updateKanjis(kanjis: List<Pair<Kanji, Boolean>>) {
        _kanjis.value = kanjis
    }

    fun updateWords(words: List<Pair<Word, Boolean>>) {
        _words.value = words
    }

    fun isKanjiMastered(id: Int) : Boolean {
        return _kanjis.value.find { it.first.id == id }?.second ?: false
    }

    fun isWordMastered(id: Int) : Boolean {
        return _words.value.find { it.first.id == id }?.second ?: false
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