plugins {
    kotlin("multiplatform") version "1.6.21"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.2"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
    id("maven-publish")
}

group = "io.github.sikrinick"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        nodejs()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
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
        val nativeMain by getting
        val nativeTest by getting


        val benchmarks by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")
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
        val benchmarksNative by creating {
            dependsOn(benchmarks)
            nativeMain.dependsOn(this)
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
        register("native")
    }
    configurations {
        val main by getting {
            outputTimeUnit = "ms"
        }
    }
}

configure<PublishingExtension> {
    val usernameKey = "SONATYPE_USERNAME"
    val passwordKey = "SONATYPE_PASSWORD"

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