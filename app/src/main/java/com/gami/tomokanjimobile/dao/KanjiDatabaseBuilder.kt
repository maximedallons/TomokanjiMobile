package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room

object KanjiDatabaseBuilder {
    private var INSTANCE: KanjiDatabase? = null

    fun getInstance(context: Context): KanjiDatabase {
        if (INSTANCE == null) {
            synchronized(KanjiDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    KanjiDatabase::class.java,
                    "kanji_db"
                ).build()
            }
        }
        return INSTANCE!!
    }
}