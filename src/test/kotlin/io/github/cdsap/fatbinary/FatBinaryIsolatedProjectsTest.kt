package io.github.cdsap.fatbinary

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Properties
import java.util.jar.JarFile

class FatBinaryIsolatedProjectsTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()

    @Test
    fun fatBinaryIsIsolatedProjectsCompatibleInMultiProjectBuild() {
        writeMultiProjectBuild()

        val runner = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("fatBinary", "--configuration-cache-problems=fail")
            .withPluginClasspath()
            .forwardOutput()

        val first = runner.build()
        assertTrue(
            "Expected Isolated Projects to be enabled:\n${first.output}",
            first.output.contains("Isolated projects is an incubating feature.", ignoreCase = true)
        )
        assertTrue(
            "Expected configuration cache store on first run:\n${first.output}",
            first.output.contains("Configuration cache entry stored.")
        )
        assertEquals(TaskOutcome.SUCCESS, first.task(":app:fatBinary")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, first.task(":tool:fatBinary")?.outcome)

        val second = runner.build()
        assertTrue(
            "Expected configuration cache HIT on second run:\n${second.output}",
            second.output.contains("Configuration cache entry reused.")
        )

        assertTrue(File(testProjectDir.root, "app/app-binary").canExecute())
        assertTrue(File(testProjectDir.root, "tool/build/tool").canExecute())
        assertBinaryOutput("app/app-binary", "hello")
        assertBinaryOutput("tool/build/tool", "tool")

        val appFatJar = File(testProjectDir.root, "app/build/libs")
            .listFiles()
            ?.single { it.extension == "jar" }
            ?: error("Expected fatJar output in app/build/libs")
        JarFile(appFatJar).use { jar ->
            assertEquals(
                "com.example.app.MainKt",
                jar.manifest.mainAttributes.getValue("Main-Class")
            )
            assertNotNull(
                "app fatJar must bundle classes from its :lib project dependency",
                jar.getEntry("com/example/lib/Greeter.class")
            )
        }

        // Replaying the cache must also work when dependency jars need to be built again.
        listOf("lib", "app", "tool").forEach {
            File(testProjectDir.root, "$it/build").deleteRecursively()
        }
        File(testProjectDir.root, "app/app-binary").delete()
        val rebuilt = runner.build()
        assertTrue(rebuilt.output, rebuilt.output.contains("Configuration cache entry reused."))
        assertEquals(TaskOutcome.SUCCESS, rebuilt.task(":app:fatBinary")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, rebuilt.task(":tool:fatBinary")?.outcome)
        assertBinaryOutput("app/app-binary", "hello")
        assertBinaryOutput("tool/build/tool", "tool")
    }

    private fun assertBinaryOutput(path: String, expected: String) {
        val process = ProcessBuilder(
            File(System.getProperty("java.home"), "bin/java").absolutePath,
            "-jar",
            File(testProjectDir.root, path).absolutePath
        )
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
        assertEquals(output, 0, process.waitFor())
        assertEquals(expected, output)
    }

    @Test
    fun pluginDescriptorDeclaresIsolatedProjectsAndConfigurationCacheSupport() {
        val descriptor = Properties()
        javaClass.classLoader
            .getResourceAsStream("META-INF/gradle-plugins/io.github.cdsap.fatbinary.properties")
            .let { requireNotNull(it) { "Missing plugin descriptor for io.github.cdsap.fatbinary" } }
            .use { descriptor.load(it) }

        assertEquals(
            "DECLARED_SUPPORTED",
            descriptor.getProperty("compatibility.feature.isolated-projects")
        )
        assertEquals(
            "DECLARED_SUPPORTED",
            descriptor.getProperty("compatibility.feature.configuration-cache")
        )
    }

    private fun writeMultiProjectBuild() {
        testProjectDir.newFile("gradle.properties").writeText(
            "org.gradle.unsafe.isolated-projects=true\n"
        )
        testProjectDir.newFile("settings.gradle").writeText(
            """
                rootProject.name = 'isolated-projects-demo'
                include 'lib', 'app', 'tool'
            """.trimIndent()
        )
        testProjectDir.newFile("build.gradle").writeText("")

        writeProject(
            name = "lib",
            buildScript = """
                plugins {
                    id 'java'
                }
            """.trimIndent(),
            sourcePath = "com/example/lib/Greeter.java",
            source = """
                package com.example.lib;

                public class Greeter {
                    public static String greet() {
                        return "hello";
                    }
                }
            """.trimIndent()
        )

        writeProject(
            name = "app",
            buildScript = """
                plugins {
                    id 'io.github.cdsap.fatbinary'
                    id 'java'
                }

                dependencies {
                    implementation project(':lib')
                }

                fatBinary {
                    mainClass = "com.example.app.Main"
                    name = "app-binary"
                }
            """.trimIndent(),
            sourcePath = "com/example/app/MainKt.java",
            source = """
                package com.example.app;

                public class MainKt {
                    public static void main(String[] args) {
                        System.out.println(com.example.lib.Greeter.greet());
                    }
                }
            """.trimIndent()
        )

        writeProject(
            name = "tool",
            buildScript = """
                plugins {
                    id 'io.github.cdsap.fatbinary'
                    id 'java'
                }

                fatBinary {
                    mainClass = "com.example.tool.Main"
                }
            """.trimIndent(),
            sourcePath = "com/example/tool/MainKt.java",
            source = """
                package com.example.tool;

                public class MainKt {
                    public static void main(String[] args) {
                        System.out.println("tool");
                    }
                }
            """.trimIndent()
        )
    }

    private fun writeProject(name: String, buildScript: String, sourcePath: String, source: String) {
        val projectDir = testProjectDir.newFolder(name)
        File(projectDir, "build.gradle").writeText(buildScript)
        val sourceFile = File(projectDir, "src/main/java/$sourcePath")
        sourceFile.parentFile.mkdirs()
        sourceFile.writeText(source)
    }
}
