package io.github.cdsap.fatbinary

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File


class FatBinaryPluginTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()

    @Test
    fun testPluginIsProperlyApplied() {
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
        testProjectDir.newFolder("src", "main", "kotlin", "com", "example")
        testProjectDir.newFile("src/main/kotlin/com/example/Main.kt").appendText(
            """
                package com.example
                fun main() {
                   println("hello")
                }
            """.trimIndent()
        )
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("fatBinary")
            .withPluginClasspath()
            .build()
        assert(File("${testProjectDir.root}/binary").exists())
    }
}
