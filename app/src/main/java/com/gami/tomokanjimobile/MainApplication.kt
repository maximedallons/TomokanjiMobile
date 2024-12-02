package com.gami.tomokanjimobile

import android.app.Application
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}