package com.gami.tomokanjimobile.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: Int,
    val login: String,
    val premium: Boolean,
    val cookie: String
)