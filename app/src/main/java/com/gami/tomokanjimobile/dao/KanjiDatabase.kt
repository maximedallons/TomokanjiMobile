package com.gami.tomokanjimobile.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.utils.Converters

@Database(entities = [Kanji::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class KanjiDatabase : RoomDatabase() {
    abstract fun kanjiDao(): KanjiDao
}