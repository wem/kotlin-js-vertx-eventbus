dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("io.vertx:vertx-core:${property("lib.version.vertx")}")
    api("io.vertx:vertx-web:${property("lib.version.vertx")}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${property("lib.version.serialization")}")
}
