package io.github.cdsap.fatbinary

internal object BinaryAssembler {

    val shellPreamble: String =
        "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n"

    fun assemble(jarBytes: ByteArray): ByteArray =
        shellPreamble.toByteArray(Charsets.US_ASCII) + jarBytes
}
