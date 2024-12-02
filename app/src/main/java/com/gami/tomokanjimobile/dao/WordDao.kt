package com.gami.tomokanjimobile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gami.tomokanjimobile.data.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM word_table")
    suspend fun getAllWords(): List<Word>

    @Query("SELECT * FROM word_table WHERE id = :id")
    suspend fun getWord(id: Int): Word

    @Query("SELECT * FROM word_table WHERE level = :level")
    suspend fun getWordsForLevel(level: Int): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()
}