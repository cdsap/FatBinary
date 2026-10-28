package io.github.cdsap.fatbinary

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

class FatBinaryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("fatBinary", FatBinaryExtension::class.java)

        target.pluginManager.withPlugin("java") {
            configureJavaPlugin(target, extension)
        }
    }

    private fun configureJavaPlugin(project: Project, extension: FatBinaryExtension) {
        val jarProvider = project.tasks.named<Jar>("jar")
        val runtimeClasspath = project.configurations.named("runtimeClasspath")

        val fatJarProvider = registerFatJar(project, extension, jarProvider, runtimeClasspath)
        registerFatBinary(project, extension, fatJarProvider)
    }

    private fun registerFatJar(
        project: Project,
        extension: FatBinaryExtension,
        jarProvider: TaskProvider<Jar>,
        runtimeClasspath: NamedDomainObjectProvider<Configuration>
    ): TaskProvider<Jar> = project.tasks.register<Jar>("fatJar") {
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

    private fun registerFatBinary(
        project: Project,
        extension: FatBinaryExtension,
        fatJarProvider: TaskProvider<Jar>
    ) {
        project.tasks.register<FatBinaryTask>("fatBinary") {
            group = "Build"
            description = "Produces a executable binary"

            dependsOn(fatJarProvider)

            val fatJarArchive = fatJarProvider.flatMap { it.archiveFile }
            this.fatJar.set(fatJarArchive)
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
