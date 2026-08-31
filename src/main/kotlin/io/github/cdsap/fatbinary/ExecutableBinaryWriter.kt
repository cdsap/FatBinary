package io.github.cdsap.fatbinary

import java.io.File

internal object ExecutableBinaryWriter {

    fun write(jarFile: File, outputFile: File) {
        outputFile.parentFile.mkdirs()
        outputFile.delete()
        outputFile.writeBytes(BinaryLauncher.assemble(jarFile.readBytes()))
        outputFile.setExecutable(true)
    }
}
