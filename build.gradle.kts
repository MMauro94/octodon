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

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    id("io.gitlab.arturbosch.detekt").version("1.23.0")
}

detekt {
    toolVersion = "1.23.0"
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
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
