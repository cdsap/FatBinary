package io.github.cdsap.fatbinary

import org.gradle.jvm.tasks.Jar
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class FatJarClasspathTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()

    @Test
    fun contentsExpandArchivesAndDirectoriesAndPreserveJarCopyRules() {
        val project = ProjectBuilder.builder().withProjectDir(testProjectDir.root).build()
        project.pluginManager.apply("java")
        val jar = project.tasks.named("jar", Jar::class.java)
        val runtimeClasspath = project.configurations.named("runtimeClasspath")
        val directory = File(testProjectDir.root, "classes")
        // Archive expansion must depend on the file type, rather than its extension.
        val archive = File(testProjectDir.root, "dependency.data")
        project.dependencies.add("runtimeOnly", project.files(directory, archive))

        val contents = FatJarClasspath.contents(project, jar, runtimeClasspath)

        directory.mkdirs()
        File(directory, "directory.txt").writeText("directory content")
        JarOutputStream(archive.outputStream()).use {
            it.putNextEntry(JarEntry("dependency.txt"))
            it.write("archive content".toByteArray())
            it.closeEntry()
        }
        val extra = testProjectDir.newFolder("extra")
        File(extra, "original.txt").writeText("project content")
        File(extra, "excluded.txt").writeText("excluded")
        jar.configure {
            from(extra) {
                into("custom")
                rename("original.txt", "renamed.txt")
                exclude("excluded.txt")
            }
        }
        jar.get()

        val destination = File(testProjectDir.root, "assembled")
        project.copy {
            with(contents)
            into(destination)
        }

        assertEquals(
            setOf("directory.txt", "dependency.txt", "custom/renamed.txt", "META-INF/MANIFEST.MF"),
            destination.walkTopDown().filter { it.isFile }
                .map { it.relativeTo(destination).invariantSeparatorsPath }.toSet()
        )
        assertEquals("directory content", File(destination, "directory.txt").readText())
        assertEquals("archive content", File(destination, "dependency.txt").readText())
        assertEquals("project content", File(destination, "custom/renamed.txt").readText())
    }
}
