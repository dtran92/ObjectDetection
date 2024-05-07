package com.dtran.scanner.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val abbreviation: String?,
    val capital: String?,
    val currency: String?,
    val name: String?,
    val phone: String?,
    val media: Media?,
    val id: Int
)

@Serializable
data class Media(
    val flag: String?,
    val emblem: String?,
    val orthographic: String?
)
