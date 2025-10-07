package parser

import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

abstract class JarZipParserUnitTestAbs {
    protected fun setupTestFile(file: File) {
        ZipOutputStream(file.outputStream()).use { zipOut ->
            zipOut.putNextEntry(ZipEntry("Class1.class"))
            zipOut.write("""
                Class1 data
            """.trimIndent().toByteArray())
            zipOut.closeEntry()

            zipOut.putNextEntry(ZipEntry("model/Class2.class"))
            zipOut.write("""
                Class2 data
            """.trimIndent().toByteArray())
            zipOut.closeEntry()

            zipOut.putNextEntry(ZipEntry("model/Class3.class"))
            zipOut.write("""
                Class3 data
            """.trimIndent().toByteArray())
            zipOut.closeEntry()

            zipOut.putNextEntry(ZipEntry("util/Class4.class"))
            zipOut.write("""
                Class4 data
            """.trimIndent().toByteArray())
            zipOut.closeEntry()

            zipOut.putNextEntry(ZipEntry("kotlin/stlib.class"))
            zipOut.write("""
                Kotlin stdlib data
            """.trimIndent().toByteArray())
            zipOut.closeEntry()

            zipOut.putNextEntry(ZipEntry("empty/"))
            zipOut.closeEntry()
        }
    }

    protected fun setupJsonFileAsResultOfTestFile(file: File) {
        file.writeText("""
            {
                "filename":"test.jar",
                "totalSize":44,
                "entries":[
                    {"path":"Class1.class","size":11,"hash":"58570a68aed02de6434b086ee42794e89297d4f69207d8346b1c2e6d3bb9cf78"},
                    {"path":"model/Class2.class","size":11,"hash":"f4002762432f422d7345d87a125cfaf14e7f88cec5e6b943f40d6ac20341611a"},
                    {"path":"model/Class3.class","size":11,"hash":"8cc92991758cfd619042b19e86287e0f352865801c450a71e6f2040d69ccb5c5"},
                    {"path":"util/Class4.class","size":11,"hash":"59b5c97390d1b96fe12adba1e409d616ea9124c30dfbf8ab3712be2fc563979e"}
                ]
            }
        """.trimIndent())
    }

    protected fun setupDifferentJsonFile(file: File) {
        file.writeText("""
            {
                "filename":"different.jar",
                "totalSize":55,
                "entries":[
                    {"path":"Class1.class","size":11,"hash":"58570a68aed02de6434b086ee42794e89297d4f69207d8346b1c2e6d3bb9cf78"},
                    {"path":"model/Class2.class","size":11,"hash":"f4002762432f422d7345d87a125cfaf14e7f88cec5e6b943f40d6ac20341611a"},
                    {"path":"model/Class3.class","size":11,"hash":"8cc92991758cfd619042b19e86287e0f352865801c450a71e6f2040d69ccb5c5"},
                    {"path":"util/Class4.class","size":11,"hash":"different hash"},
                    {"path":"service/Class6.class","size":11,"hash":"another different hash"}
                ]
            }
        """.trimIndent())
    }

    protected fun calculateExpectedHash(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(data)
        return hash.joinToString("") {
            "%02x".format(it)
        }
    }
}