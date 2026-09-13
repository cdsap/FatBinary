package io.github.cdsap.fatbinary

import org.gradle.jvm.tasks.Jar
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.jar.JarFile


class FatBinaryPluginTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()

    @Test
    fun fatBinaryWiresFatJarLazilyWithoutRealizingIt() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")
        project.pluginManager.apply(FatBinaryPlugin::class.java)
        val extension = project.extensions.getByType(FatBinaryExtension::class.java)
        extension.mainClass = "com.example.Main"
        extension.name = "binary"

        var fatJarConfigured = false
        project.tasks.named("fatJar").configure {
            fatJarConfigured = true
        }

        project.tasks.named("fatBinary", FatBinaryTask::class.java).get()

        assertFalse(
            "fatJar must stay unrealized while configuring fatBinary",
            fatJarConfigured
        )
    }

    @Test
    fun fatJarAndFatBinaryReadFromSingleCapturedExtensionInstance() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")
        project.pluginManager.apply(FatBinaryPlugin::class.java)

        val extension = project.extensions.getByType(FatBinaryExtension::class.java)
        assertSame(
            "plugin must expose the same FatBinaryExtension instance created at apply",
            extension,
            project.extensions.getByName("fatBinary")
        )

        extension.mainClass = "com.example.Owned"
        extension.name = "owned-binary"

        val fatJar = project.tasks.named("fatJar", Jar::class.java).get()
        assertEquals(
            "com.example.OwnedKt",
            fatJar.manifest.attributes.get("Main-Class")
        )

        val fatBinary = project.tasks.named("fatBinary", FatBinaryTask::class.java).get()
        assertEquals(
            project.file("owned-binary"),
            fatBinary.outputFile.get().asFile
        )
    }

    @Test
    fun testPluginIsProperlyApplied() {
        writeBuildFile(
            """
                fatBinary {
                    mainClass = "com.example.Main"
                    name = "binary"
                }
            """.trimIndent()
        )
        writeMainSource()

        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("fatBinary")
            .withPluginClasspath()
            .build()

        assertTrue(File("${testProjectDir.root}/binary").exists())
    }

    @Test
    fun fatJarWritesConfiguredMainClassToManifest() {
        writeBuildFile(
            """
                fatBinary {
                    mainClass = "com.example.Main"
                    name = "binary"
                }
            """.trimIndent()
        )
        writeMainSource()

        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("fatJar")
            .withPluginClasspath()
            .build()

        val jarFile = File(testProjectDir.root, "build/libs")
            .listFiles()
            ?.single { it.extension == "jar" }
            ?: error("Expected fatJar output in build/libs")

        JarFile(jarFile).use { jar ->
            assertEquals(
                "com.example.MainKt",
                jar.manifest.mainAttributes.getValue("Main-Class")
            )
        }
    }

    @Test
    fun fatBinaryDefaultsToBuildDirProjectNameWhenNameIsEmpty() {
        writeBuildFile(
            """
                fatBinary {
                    mainClass = "com.example.Main"
                }
            """.trimIndent()
        )
        writeMainSource()

        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("fatBinary")
            .withPluginClasspath()
            .build()

        val defaultOutput = File(testProjectDir.root, "build/${testProjectDir.root.name}")
        assertTrue(defaultOutput.exists())
    }

    private fun writeBuildFile(fatBinaryBlock: String) {
        testProjectDir.newFile("build.gradle").appendText(
            """
                plugins {
                    id 'io.github.cdsap.fatbinary'
                    id 'java'
                }
                repositories {
                    mavenCentral()
                }

                $fatBinaryBlock

            """.trimIndent()
        )
    }

    private fun writeMainSource() {
        testProjectDir.newFolder("src", "main", "kotlin", "com", "example")
        testProjectDir.newFile("src/main/kotlin/com/example/Main.kt").appendText(
            """
                package com.example
                fun main() {
                   println("hello")
                }
            """.trimIndent()
        )
    }
}
