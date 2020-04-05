buildscript {
    repositories { mavenCentral() }
    dependencies { classpath(kotlin("gradle-plugin", version = "1.3.71")) }
}

plugins {
    id("org.jetbrains.intellij") version "0.4.18"
    kotlin("jvm") version "1.3.71"
    id("io.kotlintest") version "1.1.1"

    id("se.patrikerdes.use-latest-versions") version "0.2.13"
    id("com.github.ben-manes.versions") version "0.28.0"
}

intellij {
    updateSinceUntilBuild = false
    instrumentCode = true
    version = "2019.3"
}

group = "com.nurflugel"
version = "0.1.0"

repositories {
    jcenter()
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.71")
    implementation("org.apache.commons:commons-lang3:3.10")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("io.mockk:mockk:1.9.3")
}
