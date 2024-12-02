package com.gami.tomokanjimobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "kanji_table")
@Serializable
data class Kanji(
    @PrimaryKey val id : Int = 0,
    val character : String = "-",
    val level : Int = 0,
    val meanings : List<String> = emptyList(),
    val onyomi : List<String> = emptyList(),
    val kunyomi : List<String> = emptyList(),
    val strokes : Int = 0,
)