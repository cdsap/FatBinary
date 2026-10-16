package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ExecutableBinaryBuilderTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun buildReadsJarAssemblesLauncherAndWritesExecutable() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val jarFile = tempFolder.newFile("input.jar")
        jarFile.writeBytes(jarBytes)
        val outputFile = File(tempFolder.root, "nested/output")

        ExecutableBinaryBuilder.build(jarFile, outputFile)

        assertArrayEquals(BinaryLauncher.shellPreamble.toByteArray() + jarBytes, outputFile.readBytes())
        assertTrue(outputFile.canExecute())
    }
}
