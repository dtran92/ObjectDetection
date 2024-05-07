package com.dtran.scanner.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class CountryUiModel(
    val abbreviation: String?,
    val capital: String?,
    val currency: String?,
    val name: String?,
    val phone: String?,
    val media: MediaUiModel?,
    val id: Int
)

@Serializable
data class MediaUiModel(
    val flag: String?,
    val emblem: String?,
    val orthographic: String?
)
