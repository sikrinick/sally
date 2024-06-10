import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.0.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.11"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("maven-publish")
    signing
}

group = "io.github.sikrinick"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        nodejs()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting

        val benchmarks by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.11")
            }
        }
        val benchmarksJvm by creating {
            dependsOn(benchmarks)
            jvmMain.dependsOn(this)
        }
        val benchmarksJs by creating {
            dependsOn(benchmarks)
            jsMain.dependsOn(this)
        }
    }
}

// For JVM Benchmarks based on JMH
allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvm")
        register("js")
    }
    configurations {
        val main by getting {
            outputTimeUnit = "ms"
        }
    }
}

val dokkaHtml by tasks.getting(DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

configure<PublishingExtension> {
    val usernameKey = "SONATYPE_USERNAME"
    val passwordKey = "SONATYPE_PASSWORD"

    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("${project.group}:${project.description}")
                description.set(name)
                url.set("https://github.com/sikrinick/sally")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("sikrinick")
                        name.set("Mykyta Sikriier")
                        email.set("sikrinick@gmail.com")
                    }
                }
                scm {
                    url.set("scm:git:https://github.com/sikrinick/sally.git")
                    connection.set("scm:git:https://github.com/sikrinick/sally.git")
                    developerConnection.set("scm:git:https://github.com/sikrinick/sally.git")
                }
            }
            the<SigningExtension>().sign(this)
        }
    }

    repositories {
        maven {
            credentials {
                username = findProperty(usernameKey)?.toString() ?: System.getenv(usernameKey) ?: ""
                password = findProperty(passwordKey)?.toString() ?: System.getenv(passwordKey) ?: ""
            }
            val root = "https://s01.oss.sonatype.org"
            url = uri(if (version.toString().endsWith("-SNAPSHOT")) {
                "$root/content/repositories/snapshots"
            } else {
                "$root/service/local/staging/deploy/maven2/"
            })
            print("Publishing version $version to $url")
        }
    }
}

//region Fix Gradle warning about signing tasks using publishing task outputs without explicit dependencies
// https://github.com/gradle/gradle/issues/26091
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}
//endregion