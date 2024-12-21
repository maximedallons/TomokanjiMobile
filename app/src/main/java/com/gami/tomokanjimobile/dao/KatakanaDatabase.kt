package com.gami.tomokanjimobile.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gami.tomokanjimobile.data.Hiragana
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Katakana
import com.gami.tomokanjimobile.utils.Converters

@Database(entities = [Katakana::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class KatakanaDatabase : RoomDatabase() {
    abstract fun katakanaDao(): KatakanaDao
}