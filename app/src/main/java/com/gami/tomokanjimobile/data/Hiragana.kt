package com.gami.tomokanjimobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "hiragana_table")
@Serializable
data class Hiragana (
    @PrimaryKey
    val id : Int = 0,
    val kana : String = "-",
    val romaji : String = "-",
)