package com.gami.tomokanjimobile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class SearchableViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> get() = _query

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }

    open suspend fun search(query: String) {
    }
}