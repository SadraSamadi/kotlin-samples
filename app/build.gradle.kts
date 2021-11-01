plugins {
  application
  kotlin("jvm") version "1.5.31"
  kotlin("plugin.serialization") version "1.5.31"
  id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
  implementation("com.google.guava:guava:31.0.1-jre")
  implementation("com.google.code.gson:gson:2.8.8")
  implementation("io.reactivex.rxjava3:rxjava:3.1.2")
  implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
}

application {
  mainClass.set("com.sadrasamadi.untitled.MainKt")
}
