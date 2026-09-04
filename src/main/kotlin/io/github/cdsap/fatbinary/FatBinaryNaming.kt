package io.github.cdsap.fatbinary

import java.io.File

internal object FatBinaryNaming {

    fun manifestMainClass(mainClass: String): String =
        "${mainClass}Kt"

    fun outputFile(name: String, buildDir: File, projectName: String): File =
        if (name.isNotEmpty()) {
            File(name)
        } else {
            File("${buildDir}/${projectName}")
        }
}
