package io.github.cdsap.fatbinary

import java.io.File

internal object FatBinaryOutput {

    fun resolve(projectName: String, buildDir: File, configuredName: String): File =
        if (configuredName.isNotEmpty()) {
            File(configuredName)
        } else {
            File(buildDir, projectName)
        }
}
