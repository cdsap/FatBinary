package io.github.cdsap.fatbinary

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class FatBinaryTask : DefaultTask() {

    @InputFile
    val fatJar: RegularFileProperty = project.objects.fileProperty()

    @OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    @TaskAction
    fun buildBinary() {
        val fileJar = fatJar.get()
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            delete()
            writeText("#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n")
            appendBytes(fileJar.asFile.readBytes())
            setExecutable(true)
        }
    }
}
