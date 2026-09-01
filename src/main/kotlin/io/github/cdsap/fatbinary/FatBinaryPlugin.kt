package io.github.cdsap.fatbinary

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import java.io.File

class FatBinaryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("fatBinary", FatBinaryExtension::class.java)

        val fatJarProvider = target.tasks.register<Jar>("fatJar") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            group = "Build"
            description = "Produces a fatJar "

            dependsOn(target.tasks.named("jar"))

            manifest {
                attributes("Main-Class" to resolveManifestMainClass(extension))
            }

            inputs.files(target.configurations.getByName("runtimeClasspath"))
            from(project.configurations.getByName("runtimeClasspath").map {
                if (it.isDirectory) it else project.zipTree(it)
            })
            with(project.tasks["jar"] as CopySpec)
        }

        target.tasks.register<FatBinaryTask>("fatBinary") {
            group = "Build"
            description = "Produces a executable binary"

            dependsOn(fatJarProvider)

            this.fatJar.set(fatJarProvider.get().archiveFile)
            this.outputFile.set(resolveOutputFile(extension, project))
        }
    }

    private fun resolveManifestMainClass(extension: FatBinaryExtension): String =
        "${extension.mainClass}Kt"

    private fun resolveOutputFile(extension: FatBinaryExtension, project: Project): File =
        if (extension.name.isNotEmpty()) {
            File(extension.name)
        } else {
            File("${project.buildDir}/${project.name}")
        }
}
