package io.github.cdsap.fatbinary

import java.io.File

internal object FatBinaryOutputFileResolver {

    fun resolve(configuredName: String, buildDir: File, projectName: String): File =
        if (configuredName.isNotEmpty()) {
            File(configuredName)
        } else {
            File(buildDir, projectName)
        }
}
