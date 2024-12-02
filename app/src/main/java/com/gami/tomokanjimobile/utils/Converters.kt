package com.gami.tomokanjimobile.utils

import androidx.room.TypeConverter
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.MasteredKanji
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListString(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromMasteredKanji(value: MasteredKanji): Kanji {
        return Kanji(
            value.id,
            value.character,
            value.level,
            value.meanings,
            value.onyomi,
            value.kunyomi,
            value.strokes
        )
    }
}