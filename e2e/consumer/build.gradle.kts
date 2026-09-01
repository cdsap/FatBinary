plugins {
    kotlin("jvm") version "2.1.20"
    application
    id("io.github.cdsap.fatbinary") version "1.1.0"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.github.cdsap.fatbinary.e2e.MainKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

fatBinary {
    mainClass = "io.github.cdsap.fatbinary.e2e.Main"
    name = "fatbinary-e2e"
}
