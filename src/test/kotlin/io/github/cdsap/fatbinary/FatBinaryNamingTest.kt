package io.github.cdsap.fatbinary

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FatBinaryNamingTest {

    @Test
    fun manifestMainClassAppendsKtSuffix() {
        assertEquals("com.example.MainKt", FatBinaryNaming.manifestMainClass("com.example.Main"))
    }

    @Test
    fun outputFileUsesConfiguredNameWhenPresent() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryNaming.outputFile(
            name = "binary",
            buildDir = buildDir,
            projectName = "demo"
        )

        assertEquals(File("binary"), output)
    }

    @Test
    fun outputFileDefaultsToBuildDirProjectNameWhenNameIsEmpty() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryNaming.outputFile(
            name = "",
            buildDir = buildDir,
            projectName = "demo"
        )

        assertEquals(File("${buildDir}/demo"), output)
    }
}
