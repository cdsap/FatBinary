package io.github.cdsap.fatbinary

import java.io.File

internal class ExecutableBinaryBuilder(
    private val assemble: (ByteArray) -> ByteArray = BinaryAssembler::assemble,
    private val write: (ByteArray, File) -> Unit = ExecutableBinaryWriter::write,
) {

    fun build(jarFile: File, outputFile: File) {
        val binaryBytes = assemble(jarFile.readBytes())
        write(binaryBytes, outputFile)
    }
}
