package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executors

object WordDatabaseBuilder {
    private var INSTANCE: WordDatabase? = null

    private var executor : ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun getInstance(context: Context): WordDatabase {
        if (INSTANCE == null) {
            synchronized(WordDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_db"
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