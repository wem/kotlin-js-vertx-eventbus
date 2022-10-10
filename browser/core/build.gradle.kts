kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("io.github.microutils:kotlin-logging-js:${property("lib.version.kotlin-logging")}")
                implementation(npm("@vertx/eventbus-bridge-client.js", "${property("lib.version.vertx-js-eventbus-bridge")}"))
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
