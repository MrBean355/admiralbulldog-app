rootProject.name = "admiralbulldog-app"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.10"
        id("org.jetbrains.compose") version "1.2.0-alpha01-dev748"
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
