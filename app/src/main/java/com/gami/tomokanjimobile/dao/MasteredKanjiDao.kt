package com.gami.tomokanjimobile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.MasteredKanji

@Dao
interface MasteredKanjiDao {
    @Query("SELECT * FROM mastered_kanji_table")
    suspend fun getAllKanjis(): List<MasteredKanji>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kanjis: List<MasteredKanji>)

    @Query("DELETE FROM mastered_kanji_table")
    suspend fun deleteAll()
}