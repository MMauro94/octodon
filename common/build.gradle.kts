@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.moko)
    alias(libs.plugins.sqldelight)
}

group = "io.github.mmauro94"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(17)
    }
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)
                api(libs.moko.resources)
                api(libs.moko.resources.compose)

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.encoding)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.imageLoader)

                // Database
                implementation(libs.sqldelight.adapters.primitive)
                implementation(libs.sqldelight.extensions.async)
                implementation(libs.sqldelight.extensions.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(testLibs.kotest.assertions.core)
                implementation(testLibs.kotest.framework.engine)
                implementation(testLibs.kotest.framework.datatest)
                implementation(testLibs.mockk)
                implementation(libs.ktor.client.mock)
                api(libs.moko.resources.test)
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation(libs.sqldelight.driver.android)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(testLibs.kotest.runner.junit5)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation("net.harawata:appdirs:1.2.1")
                implementation(libs.sqldelight.driver.sqlite)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(testLibs.kotest.runner.junit5)
            }
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "io.github.mmauro94.common"
}

multiplatformResources {
    multiplatformResourcesPackage = "io.github.mmauro94.common"
}

sqldelight {
    databases {
        create("Data") {
            packageName.set("io.github.mmauro94.octodon.common.db")
        }
    }
}

tasks.named<Test>("desktopTest") {
    useJUnitPlatform()
}
