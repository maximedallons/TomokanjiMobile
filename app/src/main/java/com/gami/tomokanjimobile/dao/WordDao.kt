package com.gami.tomokanjimobile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM word_table")
    suspend fun getAllWords(): List<Word>

    @Query("SELECT * FROM word_table WHERE id = :id")
    suspend fun getWord(id: Int): Word

    @Query("SELECT * FROM word_table LIMIT :limit OFFSET :offset")
    suspend fun getWordsChunk(limit: Int, offset: Int): List<Word>

    @Query("SELECT COUNT(*) FROM word_table")
    suspend fun getWordCount(): Int

    @Query("SELECT * FROM word_table WHERE level = :level")
    suspend fun getWordsForLevel(level: Int): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM word_table WHERE level = :level LIMIT :limit OFFSET :startIndex")
    suspend fun getWordsForPage(startIndex: Int, limit: Int, level: Int): List<Word>
}