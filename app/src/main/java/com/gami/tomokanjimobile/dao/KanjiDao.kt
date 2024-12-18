package com.gami.tomokanjimobile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gami.tomokanjimobile.data.Kanji

@Dao
interface KanjiDao {
    @Query("SELECT * FROM kanji_table")
    suspend fun getAllKanjis(): List<Kanji>

    @Query("SELECT * FROM kanji_table WHERE id = :id")
    suspend fun getKanji(id: Int): Kanji

    @Query("SELECT * FROM kanji_table LIMIT :limit OFFSET :offset")
    suspend fun getKanjisChunk(limit: Int, offset: Int): List<Kanji>

    @Query("SELECT COUNT(*) FROM kanji_table")
    suspend fun getKanjiCount(): Int

    @Query("SELECT * FROM kanji_table WHERE level = :level")
    suspend fun getKanjisForLevel(level: Int): List<Kanji>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kanjis: List<Kanji>)

    @Query("DELETE FROM kanji_table")
    suspend fun deleteAll()
}