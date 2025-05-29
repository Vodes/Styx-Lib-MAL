pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://repo.styx.moe/releases")
    }
}

rootProject.name = "Styx-Lib-MAL"
include(":styx-lib-mal")
