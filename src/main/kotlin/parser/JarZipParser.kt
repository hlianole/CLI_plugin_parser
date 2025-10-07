package com.hlianole.jetbrains.internship.parser

import com.hlianole.jetbrains.internship.model.ParsingResult
import com.hlianole.jetbrains.internship.model.CmpResult
import com.hlianole.jetbrains.internship.model.Entry
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

class JarZipParser {
    fun parse(file: File): ParsingResult {
        val entries = mutableListOf<Entry>()
        var totalSize = 0L

        try {
            ZipFile(file).use { zip ->
                zip.entries().asIterator().forEach { entry ->
                    if (!entry.isDirectory && !entry.name.startsWith("kotlin/")) {
                        val byteContent = zip.getInputStream(entry).readBytes()
                        val hash = calculateHash(byteContent)

                        entries.add(
                            Entry(
                                path = entry.name,
                                size = entry.size,
                                hash = hash
                            )
                        )
                        totalSize += entry.size
                    }
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error reading ${file.path}  ->  Must be .jar/.zip")
        }

        return ParsingResult(
            filename = file.name,
            totalEntries = entries.size,
            totalSize = totalSize,
            entries = entries.sortedBy {
                it.path
            }
        )
    }

    fun compare(parsingResult1: ParsingResult, parsingResult2: ParsingResult): CmpResult {
        val map1 = parsingResult1.entries.associateBy {
            it.path
        }
        val map2 = parsingResult2.entries.associateBy {
            it.path
        }

        val allPaths = (map1.keys + map2.keys).toSet()

        var commonEntries = 0
        var modified = 0
        var onlyInFirst = 0
        var onlyInSecond = 0

        allPaths.forEach { path ->
            val foundIn1 = map1[path]
            val foundIn2 = map2[path]

            when {
                foundIn1 != null && foundIn2 != null -> {
                    if (foundIn1.hash == foundIn2.hash) {
                        commonEntries++
                    } else {
                        modified++
                    }
                }
                foundIn1 != null -> onlyInFirst++
                foundIn2 != null -> onlyInSecond++
            }
        }

        val totalFiles = allPaths.size
        val strictSimilarity: Double
        val similarity: Double

        if (totalFiles > 0) {
            strictSimilarity = (commonEntries.toDouble() / totalFiles) * 100.0
            similarity = ((commonEntries + modified).toDouble() / totalFiles) * 100.0
        } else {
            strictSimilarity = 0.0
            similarity = 0.0
        }

        return CmpResult(
            file1 = parsingResult1.filename,
            file2 = parsingResult2.filename,
            strictSimilarity = strictSimilarity,
            similarity = similarity,
            commonEntries = commonEntries,
            modified = modified,
            onlyInFirst = onlyInFirst,
            onlyInSecond = onlyInSecond
        )
    }

    private fun calculateHash(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(data)
        return hash.joinToString("") {
            "%02x".format(it)
        }
    }
}