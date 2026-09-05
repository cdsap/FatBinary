package io.github.cdsap.fatbinary

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

class FatBinaryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("fatBinary", FatBinaryExtension::class.java)

        val fatJarProvider = target.tasks.register<Jar>("fatJar") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            group = "Build"
            description = "Produces a fatJar "

            dependsOn(target.tasks.named("jar"))

            manifest {
                attributes("Main-Class" to FatBinaryNaming.manifestMainClass(extension.mainClass))
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
            this.outputFile.set(
                FatBinaryNaming.outputFile(
                    name = extension.name,
                    buildDir = project.buildDir,
                    projectName = project.name
                )
            )
        }
    }
}
