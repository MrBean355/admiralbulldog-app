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
    implementation(compose.desktop.currentOs)
    implementation("com.github.mrbean355:dota2-gsi:2.1.0")
    implementation("uk.co.caprica:vlcj:4.8.2")
}