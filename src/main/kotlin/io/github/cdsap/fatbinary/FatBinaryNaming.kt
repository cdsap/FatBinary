package io.github.cdsap.fatbinary

internal object FatBinaryNaming {

    fun manifestMainClass(mainClass: String): String =
        "${mainClass}Kt"
}
