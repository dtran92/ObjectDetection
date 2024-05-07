package com.dtran.scanner.data.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class CountryEntity(
    @PrimaryKey
    val id: Int,
    val abbreviation: String?,
    val capital: String?,
    val currency: String?,
    val name: String?,
    val phone: String?,
    @Embedded
    val media: MediaEntity?,
)

@Serializable
data class MediaEntity(
    val flag: String?,
    val emblem: String?,
    val orthographic: String?
)