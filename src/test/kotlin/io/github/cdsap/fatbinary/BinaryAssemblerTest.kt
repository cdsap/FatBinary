package io.github.cdsap.fatbinary

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.text.Charsets.US_ASCII

class BinaryAssemblerTest {

    @Test
    fun shellPreambleMatchesLauncherScript() {
        assertEquals(
            "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n",
            BinaryAssembler.shellPreamble,
        )
    }

    @Test
    fun assembleStreamsPreambleThenJarBytes() {
        val jarBytes = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val output = ByteArrayOutputStream()

        BinaryAssembler.assemble(ByteArrayInputStream(jarBytes), output)

        val expectedPreamble =
            "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n".toByteArray(US_ASCII)
        assertArrayEquals(expectedPreamble + jarBytes, output.toByteArray())
    }
}
