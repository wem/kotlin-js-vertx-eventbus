dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":server:core"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:${property("lib.version.serialization")}")
}
