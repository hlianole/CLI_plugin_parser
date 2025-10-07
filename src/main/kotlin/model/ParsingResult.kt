package com.hlianole.jetbrains.internship.model

import kotlinx.serialization.Serializable

@Serializable
data class ParsingResult(
    val filename: String,
    val totalSize: Long,
    val entries: List<Entry>
)
