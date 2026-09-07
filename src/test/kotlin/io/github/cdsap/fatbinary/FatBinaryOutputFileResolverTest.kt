package io.github.cdsap.fatbinary

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FatBinaryOutputFileResolverTest {

    @Test
    fun resolveUsesConfiguredNameWhenPresent() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryOutputFileResolver.resolve(
            configuredName = "binary",
            buildDir = buildDir,
            projectName = "demo"
        )

        assertEquals(File("binary"), output)
    }

    @Test
    fun resolveDefaultsToBuildDirProjectNameWhenNameIsEmpty() {
        val buildDir = File("/tmp/build")

        val output = FatBinaryOutputFileResolver.resolve(
            configuredName = "",
            buildDir = buildDir,
            projectName = "demo"
        )

        assertEquals(File(buildDir, "demo"), output)
    }
}
