package com.hlianole.jetbrains.internship.model
import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val path: String,
    val size: Long,
    val hash: String
)
