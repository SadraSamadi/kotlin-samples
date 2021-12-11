plugins {
  application
  kotlin("jvm") version "1.6.0"
  id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.sadrasamadi"
version = "1.0.0"

application {
  mainClass.set("com.sadrasamadi.untitled.MainKt")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
  implementation("com.google.guava:guava:31.0.1-jre")
  implementation("com.google.code.gson:gson:2.8.8")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  implementation("commons-codec:commons-codec:1.15")
  implementation("io.reactivex.rxjava3:rxjava:3.1.3")
  implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
  kotlinOptions.jvmTarget = "17"
}
