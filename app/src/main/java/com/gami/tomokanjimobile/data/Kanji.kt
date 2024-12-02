package com.gami.tomokanjimobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_table")
data class Kanji(
    @PrimaryKey val id : Int = 0,
    val character : String = "-",
    val level : Int = 0,
    val meanings : List<String> = emptyList(),
    val onyomi : List<String> = emptyList(),
    val kunyomi : List<String> = emptyList(),
    val strokes : Int = 0,
)

@Entity(tableName = "mastered_kanji_table")
data class MasteredKanji(
    @PrimaryKey val id : Int = 0,
    val character : String = "-",
    val level : Int = 0,
    val meanings : List<String> = emptyList(),
    val onyomi : List<String> = emptyList(),
    val kunyomi : List<String> = emptyList(),
    val strokes : Int = 0,
)