pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("testLibs") {
            from(files("gradle/testLibs.versions.toml"))
        }
    }
}

rootProject.name = "octodon"

include(":android", ":desktop", ":common")
