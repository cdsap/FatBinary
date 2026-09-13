package io.github.cdsap.fatbinary

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.register

class FatBinaryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("fatBinary", FatBinaryExtension::class.java)

        target.pluginManager.withPlugin("java") {
            val jarProvider = target.tasks.named("jar")
            val runtimeClasspath = target.configurations.named("runtimeClasspath")

            val fatJarProvider = target.tasks.register<Jar>("fatJar") {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
                group = "Build"
                description = "Produces a fatJar "

                dependsOn(jarProvider)

                manifest {
                    attributes("Main-Class" to FatBinaryNaming.manifestMainClass(extension.mainClass))
                }

                inputs.files(runtimeClasspath)
                from(runtimeClasspath.map { configuration ->
                    configuration.map { if (it.isDirectory) it else project.zipTree(it) }
                })
                with(jarProvider.get() as CopySpec)
            }

            target.tasks.register<FatBinaryTask>("fatBinary") {
                group = "Build"
                description = "Produces a executable binary"

                dependsOn(fatJarProvider)

                this.fatJar.set(fatJarProvider.flatMap { it.archiveFile })
                this.outputFile.set(
                    project.layout.file(
                        project.provider {
                            FatBinaryOutputFileResolver.resolve(
                                configuredName = extension.name,
                                buildDir = project.layout.buildDirectory.get().asFile,
                                projectName = project.name
                            )
                        }
                    )
                )
            }
        }
    }
}
