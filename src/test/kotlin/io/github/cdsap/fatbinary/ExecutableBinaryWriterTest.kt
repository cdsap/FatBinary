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
    fun writeCreatesParentDirectoriesAssemblesBytesAndMarksExecutable() {
        val jarFile = tempFolder.newFile("input.jar")
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        jarFile.writeBytes(jarBytes)

        val outputFile = File(tempFolder.root, "nested/out/binary")

        ExecutableBinaryWriter.write(jarFile, outputFile)

        assertTrue(outputFile.parentFile.exists())
        assertArrayEquals(BinaryLauncher.assemble(jarBytes), outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }
}
