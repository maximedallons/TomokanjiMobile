package com.gami.tomokanjimobile

import androidx.lifecycle.ViewModel
import com.gami.tomokanjimobile.data.User
import kotlinx.coroutines.flow.MutableStateFlow

class SharedViewModel() : ViewModel() {
    private val _loggedUser = MutableStateFlow<User?>(null)

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