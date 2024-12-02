package com.gami.tomokanjimobile

import android.app.Application
import com.gami.tomokanjimobile.dao.MasteredKanjiDatabaseBuilder
import com.gami.tomokanjimobile.network.KanjiApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            updateMasteredKanjiDb()
        }
    }

    private suspend fun updateMasteredKanjiDb() {
        val masteredKanjiDatabase = MasteredKanjiDatabaseBuilder.getInstance(applicationContext)
        val masteredKanjiDao = masteredKanjiDatabase.masteredKanjiDao()
        val latestMasteredKanjis = KanjiApi.service.getUserKanjis(1)

        withContext(Dispatchers.IO) {
            masteredKanjiDao.deleteAll()
            masteredKanjiDao.insertAll(latestMasteredKanjis)
        }
    }
}