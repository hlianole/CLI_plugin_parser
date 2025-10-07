package com.hlianole.jetbrains.internship

import com.hlianole.jetbrains.internship.model.CmpResult
import com.hlianole.jetbrains.internship.parser.JarZipParser
import com.hlianole.jetbrains.internship.model.ParsingResult
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 3) {
        printUsage()
        exitProcess(1)
    }

    val parser = JarZipParser()

    try {
        when (args[0]) {
            "parse" -> {
                val pluginFile = File(args[1])
                val outputFile = File(args[2])

                doParse(pluginFile, parser, outputFile)
            }
            "compare" -> {
                val file1 = File(args[1])
                val file2 = File(args[2])

                doCompare(file1, file2, parser)
            }
            else -> printUsage()
        }
    } catch (e: Exception) {
        println(e.message)
    }
}




private fun doParse(
    pluginFile: File,
    parser: JarZipParser,
    outputFile: File
) {
    if (!pluginFile.exists()) {
        throw IllegalArgumentException("File not found: ${pluginFile.path}")
    }

    println("Parsing ${pluginFile.name} ...")
    val analysis = parser.parse(pluginFile)

    outputFile.writeText(
        Json.encodeToString(analysis)
    )

    println("Parsing ${pluginFile.name} completed")
    println("Saved to: ${outputFile.path}")
}

private fun doCompare(
    file1: File,
    file2: File,
    parser: JarZipParser
) {
    if (!file1.exists()) {
        val sb = StringBuilder()
        sb.append("File not found: ${file1.path}")
        if (!file2.exists()) {
            sb.append("\n")
            sb.append("File not found: ${file2.path}")
        }
        throw IllegalArgumentException(sb.lines().joinToString("\n"))
    }

    val analysis1 = parseAnalysisFile(file1)
    val analysis2 = parseAnalysisFile(file2)

    val result = parser.compare(analysis1, analysis2)

    val strictSimilarity = "%.2f".format(result.strictSimilarity)
    val similarity = "%.2f".format(result.similarity)

    printCompareResult(strictSimilarity, similarity, result, analysis1, analysis2)
}




private fun parseAnalysisFile(file: File): ParsingResult {
    try {
        return Json.decodeFromString<ParsingResult>(
            file.readText()
        )
    } catch (e: Exception) {
        throw RuntimeException("Could not read file: ${file.path}  ->  Must have JSON format and correct structure")
    }
}

private fun printCompareResult(
    strictSimilarity: String,
    similarity: String,
    result: CmpResult,
    analysis1: ParsingResult,
    analysis2: ParsingResult
) {
    println(
        """
        =========================================================================
                                                                 
          Similarity (based on identical files): ${strictSimilarity}%
          Similarity (including files with similar path): ${similarity}%
          
          Same: ${result.commonEntries}
          Modified (same path): ${result.modified}
          Common (total): ${result.commonEntries + result.modified}
                                                                 
          Only in ${analysis1.filename}: ${result.onlyInFirst}
          Only in ${analysis2.filename}: ${result.onlyInSecond}
          
        =========================================================================
    """.trimIndent()
    )
}

private fun printUsage() {
    println("""
        =========================================================================
        
          Invalid usage                                                         
          To parse file use: ./jarparser parse <file.jar/.zip> <output.json>      
          To compare file use: ./jarparser compare <output1.json> <output2.json>  
          
        =========================================================================
    """.trimIndent())
}