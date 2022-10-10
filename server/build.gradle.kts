import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    group = "ch.sourcemotion.vertx.kotlin.server"

    configureKotlin()
    afterEvaluate {
        configurePublishing()
    }
}


fun Project.configureKotlin() {
    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
                apiVersion = "1.7"
                languageVersion = "1.7"
            }
        }
    }
}

val publishUsername: String by lazy {
    "${findProperty("ossrhUsername")}"
}
val publishPassword: String by lazy {
    "${findProperty("ossrhPassword")}"
}

val publishUrl = if ("$version".endsWith("SNAPSHOT")) {
    "${property("maven.snapshot.url")}"
} else {
    "${property("maven.release.url")}"
}

fun Project.configurePublishing() {
    val publishArtifactId = "kotlin-vertx-eventbus-server-$name"
    val gitUrl = "${property("git.url")}"

    extensions.configure(PublishingExtension::class) {
        publications {
            repositories {
                maven {
                    name = "ossrh"
                    setUrl(publishUrl)
                    credentials {
                        username = publishUsername
                        password = publishPassword
                    }
                }
            }

            create<MavenPublication>(publishArtifactId) {
                from(components["kotlin"])
                artifact(tasks.getByName<Zip>("kotlinSourcesJar"))

                pom {
                    groupId = groupId
                    artifactId = publishArtifactId
                    version = "${project.version}"
                    packaging = "jar"
                    description.set("Kotlin serialization Vert.x eventbus and SockJS bridge codecs")
                    url.set(gitUrl)
                    scm {
                        connection.set("scm:$gitUrl.git")
                        developerConnection.set("scm:$gitUrl.git")
                        url.set(gitUrl)
                    }
                    licenses {
                        license {
                            name.set("The MIT License")
                            url.set("https://www.opensource.org/licenses/MIT")
                            distribution.set(gitUrl)
                        }
                    }
                    developers {
                        developer {
                            id.set("Michel Werren")
                            name.set("Michel Werren")
                            email.set("michel.werren@source-motion.ch")
                        }
                    }
                }
            }
        }
    }

    extensions.configure(SigningExtension::class) {
        sign(extensions.getByType(PublishingExtension::class).publications)
    }
}
