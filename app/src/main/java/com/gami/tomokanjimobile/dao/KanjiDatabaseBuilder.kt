package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executors

object KanjiDatabaseBuilder {
    private var INSTANCE: KanjiDatabase? = null

    private var executor : ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun getInstance(context: Context): KanjiDatabase {
        if (INSTANCE == null) {
            synchronized(KanjiDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    KanjiDatabase::class.java,
                    "kanji_db"
                )
                    .setQueryExecutor(executor.asExecutor())
                    .setTransactionExecutor(executor.asExecutor())
                    .build()
            }
        }
        return INSTANCE!!
    }

    fun setExecutor(executor: ExecutorCoroutineDispatcher) {
        this.executor = executor
    }
}