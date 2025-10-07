package parser

import com.hlianole.jetbrains.internship.parser.JarZipParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JarZipParserUnitTest: JarZipParserUnitTestAbs() {

    private val parser = JarZipParser()
    private lateinit var testDir: File


    @BeforeEach
    fun setUp() {
        testDir = Files.createTempDirectory("JarZipParserUnitTest").toFile()
    }

    @AfterEach
    fun tearDown() {
        testDir.deleteRecursively()
    }

    @Test
    fun shouldParseCorrectly() {
        val testFile = File(testDir, "test.jar")
        setupTestFile(testFile)

        val result = parser.parse(testFile)

        assertEquals("test.jar", result.filename)
        assertEquals(4, result.entries.size)
        assertEquals(44, result.totalSize)

        assertTrue(result.entries.find {
            it.path.startsWith("kotlin/")
        } == null)
        assertTrue(result.entries.find {
            it.path.startsWith("empty/")
        } == null)

        val paths = result.entries.map {
            it.path
        }.sorted()
        assertEquals(paths, listOf("Class1.class", "model/Class2.class", "model/Class3.class", "util/Class4.class"))

        val hashes = listOf(
            calculateExpectedHash("Class1 data".toByteArray()),
            calculateExpectedHash("Class2 data".toByteArray()),
            calculateExpectedHash("Class3 data".toByteArray()),
            calculateExpectedHash("Class4 data".toByteArray()),
        )
        assertEquals(hashes, result.entries.sortedBy {
            it.path
        }.map {
            it.hash
        })
    }

    @Test
    fun shouldThrowExceptionOnParsingInvalidFile() {
        val invalid = File(testDir, "invalid.txt")
        invalid.writeText("Invalid file. Should not read")

        assertThrows<RuntimeException> {
            parser.parse(invalid)
        }
    }

    @Test
    fun shouldReturnIdenticalOnComparingSameFile() {
        val testJsonFile = File(testDir, "test.json")
        setupJsonFileAsResultOfTestFile(testJsonFile)

        val result = parser.compare(testJsonFile, testJsonFile)

        assertTrue(result.similarity > 99.9)
        assertTrue(result.strictSimilarity > 99.9)

        assertEquals(4, result.commonEntries)
        assertEquals(0, result.onlyInFirst)
        assertEquals(0, result.onlyInSecond)

        assertEquals("test.jar", result.file1)
        assertEquals(result.file1, result.file2)
    }

    @Test
    fun shouldCompareCorrectly() {
        val testJsonFile = File(testDir, "test.json")
        setupJsonFileAsResultOfTestFile(testJsonFile)

        val differentJsonFile = File(testDir, "different.json")
        setupDifferentJsonFile(differentJsonFile)

        val result = parser.compare(testJsonFile, differentJsonFile)

        assertTrue(result.similarity > 79.9)
        assertTrue(result.similarity < 80.1)
        assertTrue(result.strictSimilarity > 59.9)
        assertTrue(result.strictSimilarity < 60.1)

        assertEquals(3, result.commonEntries)
        assertEquals(1, result.modified)
        assertEquals(0, result.onlyInFirst)
        assertEquals(1, result.onlyInSecond)
    }

    @Test
    fun shouldThrowExceptionOnComparingWithInvalidFile() {
        val testJsonFile = File(testDir, "test.json")
        setupJsonFileAsResultOfTestFile(testJsonFile)

        val invalid = File(testDir, "invalid.txt")
        invalid.writeText("Invalid file. Should not read")

        assertThrows<RuntimeException> {
            parser.compare(testJsonFile, invalid)
        }
    }

}