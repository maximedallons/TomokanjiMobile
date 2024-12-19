package com.gami.tomokanjimobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class WordKanji(
    val text: String,
    val common: Boolean
)

@Serializable
data class WordKana(
    val text: String,
    val common: Boolean
)

@Serializable
data class Example(
    val text: String,
    val sentences: List<Sentence>
)

@Serializable
data class Sentence(
    val lang: String,
    val text: String
)

@Entity(tableName = "word_table")
@Serializable
data class Word(
    @PrimaryKey val id: Int = 0,
    val kanjis: List<WordKanji> = emptyList(),
    val kanas: List<WordKana> = emptyList(),
    val translations: List<String> = emptyList(),
    val examples: List<Example> = emptyList(),
    val level: Int = 0,
)