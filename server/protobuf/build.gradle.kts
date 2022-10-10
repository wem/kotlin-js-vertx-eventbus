dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":server:core"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${property("lib.version.serialization")}")
}
