package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room

object WordDatabaseBuilder {
    private var INSTANCE: WordDatabase? = null

    fun getInstance(context: Context): WordDatabase {
        if (INSTANCE == null) {
            synchronized(WordDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_db"
                ).build()
            }
        }
        return INSTANCE!!
    }
}