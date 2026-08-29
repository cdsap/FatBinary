package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BinaryLauncherTest {

    @Test
    fun shellPreambleContainsShebangAndJavaExec() {
        val preamble = BinaryLauncher.shellPreamble

        assertTrue(preamble.startsWith("#!/bin/sh\n"))
        assertTrue(preamble.contains("exec java \$JAVA_OPTS -jar \$0 \"\$@\""))
    }

    @Test
    fun assemblePrependsPreambleToJarBytes() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val binary = BinaryLauncher.assemble(jarBytes)

        val expected = BinaryLauncher.shellPreamble.toByteArray() + jarBytes
        assertArrayEquals(expected, binary)
    }
}
