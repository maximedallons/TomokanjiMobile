package com.gami.tomokanjimobile.ui.composables.kanjis

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class KanjiViewModel : ViewModel() {
    private val _showKunyomi = MutableStateFlow(true)
    val showKunyomi: StateFlow<Boolean> = _showKunyomi

    private val _currentLevel = MutableStateFlow(5)
    val currentLevel: StateFlow<Int> = _currentLevel

    // Update the showKunyomi state
    fun toggleShowKunyomi() {
        _showKunyomi.update { !it }
    }

    // Update the currentLevel state
    fun updateCurrentLevel(level: Int) {
        _currentLevel.update { level }
    }
}