import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "2.1.1"
}

group = "io.github.cdsap"
version = "1.1.0"

gradlePlugin {
    website.set("https://github.com/cdsap/FatBinary")
    vcsUrl.set("https://github.com/cdsap/FatBinary")
    plugins {
        create("FatBinaryPlugin") {
            id = "io.github.cdsap.fatbinary"
            displayName = "FatBinary executables"
            description = "Creates an executable binary with all the dependencies"
            implementationClass = "io.github.cdsap.fatbinary.FatBinaryPlugin"
            tags.set(listOf("binary", "executable"))
            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}
