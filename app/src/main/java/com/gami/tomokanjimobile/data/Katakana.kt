package com.gami.tomokanjimobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "katakana_table")
@Serializable
data class Katakana (
    @PrimaryKey
    val id : Int = 0,
    val kana : String = "-",
    val romaji : String = "-",
)