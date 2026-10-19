package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BinaryAssemblerTest {

    @Test
    fun shellPreambleContainsShebangAndJavaExec() {
        val preamble = BinaryAssembler.shellPreamble

        assertTrue(preamble.startsWith("#!/bin/sh\n"))
        assertTrue(preamble.contains("exec java \$JAVA_OPTS -jar \$0 \"\$@\""))
    }

    @Test
    fun assemblePrependsPreambleToJarBytes() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val binary = BinaryAssembler.assemble(jarBytes)

        val expected = BinaryAssembler.shellPreamble.toByteArray() + jarBytes
        assertArrayEquals(expected, binary)
    }
}
