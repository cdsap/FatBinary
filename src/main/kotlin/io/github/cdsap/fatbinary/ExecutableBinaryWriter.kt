package io.github.cdsap.fatbinary

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal object ExecutableBinaryWriter {
    fun write(bytes: ByteArray, outputFile: File) {
        outputFile.parentFile?.mkdirs()

        val outputPath = outputFile.toPath()
        val temporaryPath = Files.createTempFile(
            outputPath.toAbsolutePath().parent,
            "${outputFile.name}.",
            ".tmp",
        )

        try {
            Files.write(temporaryPath, bytes)
            temporaryPath.toFile().setExecutable(true)
            Files.move(temporaryPath, outputPath, StandardCopyOption.REPLACE_EXISTING)
        } finally {
            try {
                Files.deleteIfExists(temporaryPath)
            } catch (_: IOException) {
                // Preserve the original write or replacement failure.
            }
        }
    }
}
