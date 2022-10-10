plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf-js:${property("lib.version.serialization")}")
                api(project(":browser:core"))
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${property("lib.version.coroutines")}")
                implementation(devNpm("puppeteer", "${property("lib.test.version.puppeteer")}"))
                implementation(devNpm("sockjs", "${property("lib.test.version.sockjs")}"))
            }
        }
    }
}
