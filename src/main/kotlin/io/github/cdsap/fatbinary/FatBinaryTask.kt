package io.github.cdsap.fatbinary

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class FatBinaryTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @get:InputFile
    val fatJar: RegularFileProperty = objects.fileProperty()

    @get:OutputFile
    val outputFile: RegularFileProperty = objects.fileProperty()

    @TaskAction
    fun buildBinary() {
        ExecutableBinaryWriter.materialize(
            jarFile = fatJar.get().asFile,
            outputFile = outputFile.get().asFile
        )
    }
}
