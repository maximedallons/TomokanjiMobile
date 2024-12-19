package com.gami.tomokanjimobile.utils

import androidx.room.TypeConverter
import com.gami.tomokanjimobile.data.*
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

    @TypeConverter
    fun fromJsonToUser(json: String?): User? {
        return Gson().fromJson(json, object : TypeToken<User>() {}.type)
    }

    @TypeConverter
    fun fromExamplesListToJson(examples: List<Example>?): String? {
        return Gson().toJson(examples)
    }

    @TypeConverter
    fun fromJsonToExamplesList(json: String?): List<Example>? {
        return Gson().fromJson(json, object : TypeToken<List<Example>>() {}.type)
    }

    @TypeConverter
    fun fromSentencesListToJson(sentences: List<Sentence>?): String? {
        return Gson().toJson(sentences)
    }

    @TypeConverter
    fun fromJsonToSentencesList(json: String?): List<Sentence>? {
        return Gson().fromJson(json, object : TypeToken<List<Sentence>>() {}.type)
    }
}