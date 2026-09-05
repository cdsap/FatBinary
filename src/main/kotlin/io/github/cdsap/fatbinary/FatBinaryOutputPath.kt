package io.github.cdsap.fatbinary

import java.io.File

internal object FatBinaryOutputPath {

    fun resolve(configuredName: String, projectName: String, buildDir: File): File =
        if (configuredName.isNotEmpty()) {
            File(configuredName)
        } else {
            File("${buildDir}/${projectName}")
        }
}
