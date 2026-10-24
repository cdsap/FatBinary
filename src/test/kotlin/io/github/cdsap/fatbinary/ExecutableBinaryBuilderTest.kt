package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ExecutableBinaryBuilderTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun buildAssemblesJarBytesAndWritesResult() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val assembledBytes = byteArrayOf(0x23, 0x21)
        val jarFile = tempFolder.newFile("input.jar")
        jarFile.writeBytes(jarBytes)
        val outputFile = File(tempFolder.root, "nested/output")
        var assembledInput: ByteArray? = null
        var writtenBytes: ByteArray? = null
        var writtenFile: File? = null

        val builder = ExecutableBinaryBuilder(
            assemble = { bytes ->
                assembledInput = bytes
                assembledBytes
            },
            write = { bytes, file ->
                writtenBytes = bytes
                writtenFile = file
            },
        )

        builder.build(jarFile, outputFile)

        assertArrayEquals(jarBytes, assembledInput)
        assertArrayEquals(assembledBytes, writtenBytes)
        assertEquals(outputFile, writtenFile)
    }
}
