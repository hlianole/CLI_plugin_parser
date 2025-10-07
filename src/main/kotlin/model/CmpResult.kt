package com.hlianole.jetbrains.internship.model

data class CmpResult(
    val file1: String,
    val file2: String,
    val strictSimilarity: Double,
    val similarity: Double,
    val commonEntries: Int,
    val modified: Int,
    val onlyInFirst: Int,
    val onlyInSecond: Int
)
