package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room

object MasteredKanjiDatabaseBuilder {
    private var INSTANCE: MasteredKanjiDatabase? = null

    fun getInstance(context: Context): MasteredKanjiDatabase {
        if (INSTANCE == null) {
            synchronized(MasteredKanjiDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MasteredKanjiDatabase::class.java,
                    "mastered_kanji_db"
                ).build()
            }
        }
        return INSTANCE!!
    }
}