package com.gami.tomokanjimobile.dao

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executors

object KatakanaDatabaseBuilder {
    private var INSTANCE: KatakanaDatabase? = null

    private var executor : ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun getInstance(context: Context): KatakanaDatabase {
        if (INSTANCE == null) {
            synchronized(KatakanaDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    KatakanaDatabase::class.java,
                    "katakana_db"
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