import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"
}

group = "io.github.cdsap"
version = "1.0-SNAPSHOT"


gradlePlugin {
    plugins {
        create("FatBinaryPlugin") {
            id = "io.github.cdsap.fatbinary"
            displayName = "FatBinary executables"
            description = "Creates an executable binary with all the dependencies"
            implementationClass = "io.github.cdsap.fatbinary.FatBinaryPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/cdsap/FatBinary"
    vcsUrl = "https://github.com/cdsap/FatBinary"
    tags = listOf("binary", "executable")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
