package io.github.cdsap.fatbinary

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FatBinaryOutputPathTest {

    @Test
    fun resolveUsesConfiguredNameWhenPresent() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryOutputPath.resolve(
            configuredName = "binary",
            projectName = "demo",
            buildDir = buildDir
        )

        assertEquals(File("binary"), output)
    }

    @Test
    fun resolveDefaultsToBuildDirProjectNameWhenNameIsEmpty() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryOutputPath.resolve(
            configuredName = "",
            projectName = "demo",
            buildDir = buildDir
        )

        assertEquals(File("${buildDir}/demo"), output)
    }
}
