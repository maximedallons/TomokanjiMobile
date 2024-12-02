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

    @Query("SELECT * FROM kanji_table WHERE level = :level")
    suspend fun getKanjisForLevel(level: Int): List<Kanji>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kanjis: List<Kanji>)

    @Query("DELETE FROM kanji_table")
    suspend fun deleteAll()
}