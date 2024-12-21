package com.gami.tomokanjimobile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gami.tomokanjimobile.data.Hiragana

@Dao
interface HiraganaDao {
    @Query("SELECT * FROM hiragana_table")
    suspend fun getAllHiraganas(): List<Hiragana>

    @Query("SELECT * FROM hiragana_table WHERE id = :id")
    suspend fun getHiragana(id: Int): Hiragana

    @Query("SELECT * FROM hiragana_table LIMIT :limit OFFSET :offset")
    suspend fun getHiraganasChunk(limit: Int, offset: Int): List<Hiragana>

    @Query("SELECT COUNT(*) FROM hiragana_table")
    suspend fun getHiraganaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hiraganas: List<Hiragana>)

    @Query("DELETE FROM hiragana_table")
    suspend fun deleteAll()
}