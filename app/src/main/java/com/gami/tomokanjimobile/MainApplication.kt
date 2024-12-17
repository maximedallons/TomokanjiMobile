package com.gami.tomokanjimobile

import android.app.Application
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.dao.KanjiDatabaseBuilder
import com.gami.tomokanjimobile.dao.WordDatabaseBuilder
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.network.KanjiApi
import com.gami.tomokanjimobile.network.WordApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //Get the kanji and word database
        CoroutineScope(Dispatchers.IO).launch {
            //if the kanji and word database is empty, fill it with data from the API
            val kanjiDao = KanjiDatabaseBuilder.getInstance(applicationContext).kanjiDao()
            val wordDao = WordDatabaseBuilder.getInstance(applicationContext).wordDao()
            if (kanjiDao.getAllKanjis().isEmpty()) {
                val kanjiList = KanjiApi.service.getKanjis()
                kanjiDao.insertAll(kanjiList)
            }
            if (wordDao.getAllWords().isEmpty()) {
                val wordList = WordApi.service.getWords()
                wordDao.insertAll(wordList)
            }
        }
    }
}