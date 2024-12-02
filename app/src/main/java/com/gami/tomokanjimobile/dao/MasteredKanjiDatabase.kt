package com.gami.tomokanjimobile.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.MasteredKanji
import com.gami.tomokanjimobile.utils.Converters

@Database(entities = [MasteredKanji::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MasteredKanjiDatabase : RoomDatabase() {
    abstract fun masteredKanjiDao(): MasteredKanjiDao
}