import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension

subprojects {
    apply(plugin = "org.jetbrains.kotlin.js")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    group = "ch.sourcemotion.vertx.kotlin.browser"

    configureKotlin()
    afterEvaluate {
        configurePublishing()
    }
}


fun Project.configureKotlin() {
    extensions.configure(KotlinJsProjectExtension::class) {
        js(BOTH) {
            compilations.all {
                compileKotlinTask.kotlinOptions {
                    moduleKind = "umd"
                    apiVersion = "1.7"
                    languageVersion = "1.7"
                    freeCompilerArgs += listOf(
                        "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                        "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
                    )
                }
            }
            browser {
                testTask {
                    useKarma {
                        useChromiumHeadless()
                    }
                }
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
    val publishArtifactId = "kotlin-vertx-eventbus-browser-$name"
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
                artifact(tasks.getByName<Zip>("jsLegacySourcesJar"))

                pom {
                    groupId = groupId
                    artifactId = publishArtifactId
                    version = "${project.version}"
                    packaging = "jar"
                    description.set("Vert.x eventbus SockJS bridge Kotlin JS wrapper with Kotlin serialization integration")
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
