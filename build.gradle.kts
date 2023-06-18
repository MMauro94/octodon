import io.gitlab.arturbosch.detekt.Detekt

group = "io.github.mmauro94"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.detekt)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

tasks.withType<Detekt>().configureEach {
    setSource(files(rootDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("build/")
    exclude("*/build/")

    reports {
        html.required.set(true)
        md.required.set(true)
    }
}
