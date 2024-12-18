package com.gami.tomokanjimobile.ui.composables.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ViewModel for managing the bottom navigation buttons
class BottomNavigationViewModel : ViewModel() {
    // Backing property for the state of additional center buttons
    private val _additionalCenterButtons = MutableStateFlow<List<CircleButton>>(emptyList())

    // Publicly exposed StateFlow for observing updates
    val additionalCenterButtons: StateFlow<List<CircleButton>> get() = _additionalCenterButtons

    // Adds a new center button
    fun addCenterButton(button: CircleButton) {
        _additionalCenterButtons.value += button
    }

    // Clears all center buttons
    fun clearCenterButtons() {
        _additionalCenterButtons.value = emptyList()
    }
}