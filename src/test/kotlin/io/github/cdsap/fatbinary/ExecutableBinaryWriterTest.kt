package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ExecutableBinaryWriterTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun writeCreatesParentDirectoriesWritesBytesAndMarksExecutable() {
        val bytes = byteArrayOf(0x23, 0x21, 0x2F, 0x62, 0x69, 0x6E, 0x2F)
        val outputFile = File(tempFolder.root, "nested/out/binary")

        ExecutableBinaryWriter.write(bytes, outputFile)

        assertTrue(outputFile.parentFile.exists())
        assertArrayEquals(bytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }
}
