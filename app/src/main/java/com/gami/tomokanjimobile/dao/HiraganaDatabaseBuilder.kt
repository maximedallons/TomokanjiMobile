package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executors

object HiraganaDatabaseBuilder {
    private var INSTANCE: HiraganaDatabase? = null

    private var executor : ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun getInstance(context: Context): HiraganaDatabase {
        if (INSTANCE == null) {
            synchronized(HiraganaDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    HiraganaDatabase::class.java,
                    "hiragana_db"
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