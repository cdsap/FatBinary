package io.github.cdsap.fatbinary

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import java.io.File

class FatBinaryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("fatBinary", FatBinaryExtension::class.java)

        val fatJarProvider = target.tasks.register<Jar>("fatJar") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            group = "Build"
            description = "Produces a fatJar "

            dependsOn(target.tasks.named("jar"))

            manifest {
                attributes("Main-Class" to "${project.extensions.getByType<FatBinaryExtension>().mainClass}Kt")
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

            if(project.extensions.getByType<FatBinaryExtension>().name.isNotEmpty()){
                this.outputFile.set(File(project.extensions.getByType<FatBinaryExtension>().name))
            } else {
                this.outputFile.set(File("${project.buildDir}/${project.name}"))
            }

        }
    }
}
