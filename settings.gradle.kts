rootProject.name = "admiralbulldog-app"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.8.20"
        kotlin("plugin.serialization") version "1.8.20"
        id("org.jetbrains.compose") version "1.4.3"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include("data", "app")
