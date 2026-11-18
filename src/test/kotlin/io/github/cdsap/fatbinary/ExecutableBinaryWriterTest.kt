package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import java.io.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.text.Charsets.US_ASCII

class ExecutableBinaryWriterTest {

    private val launcherPreamble =
        "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n".toByteArray(US_ASCII)

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun materializeStreamsPreambleThenJarAndWritesExecutable() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val jarFile = tempFolder.newFile("input.jar")
        jarFile.writeBytes(jarBytes)
        val outputFile = File(tempFolder.root, "nested/output")

        ExecutableBinaryWriter.materialize(jarFile, outputFile)

        assertArrayEquals(launcherPreamble + jarBytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }

    @Test
    fun materializeCreatesParentDirectories() {
        val jarFile = tempFolder.newFile("input.jar")
        val outputFile = File(tempFolder.root, "nested/out/binary")

        ExecutableBinaryWriter.materialize(jarFile, outputFile)

        assertTrue(outputFile.parentFile.exists())
    }

    @Test
    fun materializeReplacesExistingBytesAndKeepsOutputExecutable() {
        val jarBytes = byteArrayOf(0x6E, 0x65, 0x77)
        val jarFile = tempFolder.newFile("input.jar")
        jarFile.writeBytes(jarBytes)
        val outputFile = File(tempFolder.root, "binary")
        outputFile.writeBytes(byteArrayOf(0x6F, 0x6C, 0x64))
        outputFile.setExecutable(true)

        ExecutableBinaryWriter.materialize(jarFile, outputFile)

        assertArrayEquals(launcherPreamble + jarBytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }

    @Test
    fun materializeParentlessRelativePathWritesBytesAndMarksExecutable() {
        val jarBytes = byteArrayOf(0x62, 0x69, 0x6E, 0x2F)
        val jarFile = tempFolder.newFile("input.jar")
        jarFile.writeBytes(jarBytes)
        val outputFile = File("executable-binary-writer-parentless-test")

        try {
            ExecutableBinaryWriter.materialize(jarFile, outputFile)

            assertArrayEquals(launcherPreamble + jarBytes, outputFile.readBytes())
            assertTrue(outputFile.canExecute())
        } finally {
            outputFile.delete()
        }
    }

    @Test
    fun materializeSurfacesParentDirectoryCreationFailure() {
        val jarFile = tempFolder.newFile("input.jar")
        val parentFile = tempFolder.newFile("parent")
        val outputFile = File(parentFile, "binary")

        assertThrows(IOException::class.java) {
            ExecutableBinaryWriter.materialize(jarFile, outputFile)
        }
    }
}
