package io.github.cdsap.fatbinary

import java.io.File

internal object ExecutableBinaryWriter {

    fun write(bytes: ByteArray, outputFile: File) {
        outputFile.parentFile.mkdirs()
        outputFile.delete()
        outputFile.writeBytes(bytes)
        outputFile.setExecutable(true)
    }
}
