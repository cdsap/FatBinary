package io.github.cdsap.fatbinary

import java.io.File

internal object ExecutableBinaryBuilder {

    fun build(jarFile: File, outputFile: File) {
        val jarBytes = jarFile.readBytes()
        val binaryBytes = BinaryAssembler.assemble(jarBytes)
        ExecutableBinaryWriter.write(
            bytes = binaryBytes,
            outputFile = outputFile
        )
    }
}
