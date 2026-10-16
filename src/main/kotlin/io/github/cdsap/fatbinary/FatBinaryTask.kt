package io.github.cdsap.fatbinary

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class FatBinaryTask : DefaultTask() {

    @get:InputFile
    abstract val fatJar: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun buildBinary() {
        ExecutableBinaryBuilder.build(
            jarFile = fatJar.get().asFile,
            outputFile = outputFile.get().asFile
        )
    }
}
