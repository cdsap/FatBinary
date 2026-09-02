package io.github.cdsap.fatbinary

import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FatBinaryConfigurationCacheTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()

    @Test
    fun fatBinaryIsConfigurationCacheCompatible() {
        writeBuildFile()
        writeMainSource()

        val runner = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(
                "fatBinary",
                "--configuration-cache",
                "--configuration-cache-problems=fail"
            )
            .withPluginClasspath()
            .forwardOutput()

        val first = runner.build()
        assertTrue(
            "Expected configuration cache store on first run:\n${first.output}",
            first.output.contains("Configuration cache entry stored.")
        )

        val second = runner.build()
        assertTrue(
            "Expected configuration cache HIT on second run:\n${second.output}",
            second.output.contains("Configuration cache entry reused.")
        )
        assertTrue(File(testProjectDir.root, "binary").exists())
    }

    private fun writeBuildFile() {
        testProjectDir.newFile("build.gradle").appendText(
            """
                plugins {
                    id 'io.github.cdsap.fatbinary'
                    id 'java'
                }
                repositories {
                    mavenCentral()
                }

                fatBinary {
                    mainClass = "com.example.Main"
                    name = "binary"
                }

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
