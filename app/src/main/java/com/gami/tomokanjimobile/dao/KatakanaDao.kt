package com.gami.tomokanjimobile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gami.tomokanjimobile.data.Katakana

@Dao
interface KatakanaDao {
    @Query("SELECT * FROM katakana_table")
    suspend fun getAllKatakanas(): List<Katakana>

    @Query("SELECT * FROM katakana_table WHERE id = :id")
    suspend fun getKatakana(id: Int): Katakana

    @Query("SELECT * FROM katakana_table LIMIT :limit OFFSET :offset")
    suspend fun getKatakanasChunk(limit: Int, offset: Int): List<Katakana>

    @Query("SELECT COUNT(*) FROM katakana_table")
    suspend fun getKatakanaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(katakanas: List<Katakana>)

    @Query("DELETE FROM katakana_table")
    suspend fun deleteAll()
}