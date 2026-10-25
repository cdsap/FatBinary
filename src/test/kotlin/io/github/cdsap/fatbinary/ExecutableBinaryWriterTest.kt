package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import java.io.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ExecutableBinaryWriterTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun materializeReadsJarAssemblesBinaryAndWritesExecutable() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val jarFile = tempFolder.newFile("input.jar")
        jarFile.writeBytes(jarBytes)
        val outputFile = File(tempFolder.root, "nested/output")

        ExecutableBinaryWriter.materialize(jarFile, outputFile)

        assertArrayEquals(BinaryAssembler.shellPreamble.toByteArray() + jarBytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }

    @Test
    fun writeCreatesParentDirectoriesWritesBytesAndMarksExecutable() {
        val bytes = byteArrayOf(0x23, 0x21, 0x2F, 0x62, 0x69, 0x6E, 0x2F)
        val outputFile = File(tempFolder.root, "nested/out/binary")

        ExecutableBinaryWriter.write(bytes, outputFile)

        assertTrue(outputFile.parentFile.exists())
        assertArrayEquals(bytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }

    @Test
    fun writeReplacesExistingBytesAndKeepsOutputExecutable() {
        val outputFile = File(tempFolder.root, "binary")
        outputFile.writeBytes(byteArrayOf(0x6F, 0x6C, 0x64))
        outputFile.setExecutable(true)
        val bytes = byteArrayOf(0x6E, 0x65, 0x77)

        ExecutableBinaryWriter.write(bytes, outputFile)

        assertArrayEquals(bytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }

    @Test
    fun writeParentlessRelativePathWritesBytesAndMarksExecutable() {
        val bytes = byteArrayOf(0x62, 0x69, 0x6E, 0x2F)
        val outputFile = File("executable-binary-writer-parentless-test")

        try {
            ExecutableBinaryWriter.write(bytes, outputFile)

            assertArrayEquals(bytes, outputFile.readBytes())
            assertTrue(outputFile.canExecute())
        } finally {
            outputFile.delete()
        }
    }

    @Test
    fun writeSurfacesParentDirectoryCreationFailure() {
        val parentFile = tempFolder.newFile("parent")
        val outputFile = File(parentFile, "binary")

        assertThrows(IOException::class.java) {
            ExecutableBinaryWriter.write(byteArrayOf(0x01), outputFile)
        }
    }
}
