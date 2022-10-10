plugins {
    id("org.jetbrains.kotlin.js") version "1.7.20" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20" apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}
