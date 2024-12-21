package com.gami.tomokanjimobile.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class Kana(
    open var id: Int = 0,
    open var kana: String = "-",
    open var romaji: String = "-",
)