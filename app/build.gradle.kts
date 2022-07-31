import org.jetbrains.compose.compose

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.github.mrbean355"
version = "2.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "com.github.mrbean355.bulldog.MainKt"
    }
}

dependencies {
    implementation(project(":data"))
    implementation(kotlin("reflect"))
    implementation(compose.desktop.currentOs)
    implementation("com.github.mrbean355:dota2-gsi:2.0.0")
}