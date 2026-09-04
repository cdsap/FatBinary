package io.github.cdsap.fatbinary

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FatBinaryOutputTest {

    @Test
    fun resolveUsesConfiguredNameWhenPresent() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryOutput.resolve(
            projectName = "demo",
            buildDir = buildDir,
            configuredName = "binary"
        )

        assertEquals(File("binary"), output)
    }

    @Test
    fun resolveDefaultsToBuildDirProjectNameWhenConfiguredNameIsEmpty() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryOutput.resolve(
            projectName = "demo",
            buildDir = buildDir,
            configuredName = ""
        )

        assertEquals(File(buildDir, "demo"), output)
    }
}
