package com.gami.tomokanjimobile.utils

import androidx.room.TypeConverter
import com.gami.tomokanjimobile.data.WordKana
import com.gami.tomokanjimobile.data.WordKanji
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
    fun fromStringBooleanListToJson(wordKanjis: List<Map<String, Boolean>>?): String? {
        return Gson().toJson(wordKanjis)
    }

    @TypeConverter
    fun fromJsonToStringBooleanList(json: String?): List<Map<String, Boolean>>? {
        return Gson().fromJson(json, object : TypeToken<List<Map<String, Boolean>>>() {}.type)
    }

    @TypeConverter
    fun fromKanjiListToJson(kanjis: List<WordKanji>?): String? {
        return Gson().toJson(kanjis)
    }

    @TypeConverter
    fun fromJsonToKanjiList(json: String?): List<WordKanji>? {
        return Gson().fromJson(json, object : TypeToken<List<WordKanji>>() {}.type)
    }


    @TypeConverter
    fun fromKanaListToJson(kanjis: List<WordKana>?): String? {
        return Gson().toJson(kanjis)
    }

    @TypeConverter
    fun fromJsonToKanaList(json: String?): List<WordKana>? {
        return Gson().fromJson(json, object : TypeToken<List<WordKana>>() {}.type)
    }
}