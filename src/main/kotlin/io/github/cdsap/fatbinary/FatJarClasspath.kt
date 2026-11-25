package io.github.cdsap.fatbinary

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import java.io.File
import javax.inject.Inject

internal object FatJarClasspath {

    fun mapEntries(files: Iterable<File>, asZipTree: (File) -> Any): List<Any> =
        files.map { if (it.isDirectory) it else asZipTree(it) }

    fun contents(
        project: Project,
        jar: TaskProvider<Jar>,
        runtimeClasspath: NamedDomainObjectProvider<Configuration>
    ): CopySpec {
        val contents = project.copySpec()
        val archives = project.objects.newInstance(ArchiveServices::class.java).archives
        // Keep producer metadata so Gradle defers this transform until dependency outputs exist.
        contents.from(runtimeClasspath.flatMap { it.elements }.map { entries ->
            mapEntries(entries.map { it.asFile }) { archives.zipTree(it) }
        })
        // The jar dependency is realized before execution, without forcing it when fatJar is configured.
        jar.configure { contents.with(this) }
        return contents
    }

    internal abstract class ArchiveServices {
        @get:Inject
        abstract val archives: ArchiveOperations
    }
}
