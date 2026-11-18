package io.github.cdsap.fatbinary

import org.junit.Assert.assertEquals
import org.junit.Test

class BinaryAssemblerTest {

    @Test
    fun shellPreambleMatchesLauncherScript() {
        assertEquals(
            "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n",
            BinaryAssembler.shellPreamble,
        )
    }
}
