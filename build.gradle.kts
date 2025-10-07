plugins {
    kotlin("jvm") version "2.1.10"
    // https://kotlinlang.org/docs/serialization.html#serialize-and-deserialize-json
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.hlianole.jetbrains.internship"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // https://kotlinlang.org/docs/serialization.html#serialize-and-deserialize-json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes["Main-Class"] = "com.hlianole.jetbrains.internship.MainKt"
        }
        archiveFileName.set("app.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) {
                it
            }
            else {
                zipTree(it)
            }
        })
    }
}
kotlin {
    jvmToolchain(21)
}