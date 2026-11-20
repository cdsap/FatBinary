package io.github.cdsap.fatbinary

import java.io.InputStream
import java.io.OutputStream

internal object BinaryAssembler {

    val shellPreamble: String =
        "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n"

    fun assemble(jarInput: InputStream, output: OutputStream) {
        output.write(shellPreamble.toByteArray(Charsets.US_ASCII))
        jarInput.copyTo(output)
    }
}
