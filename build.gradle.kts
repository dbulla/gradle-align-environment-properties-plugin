buildscript{
    repositories{mavenCentral()}
    dependencies{classpath(kotlin("gradle-plugin",version="1.3.70"))}
}

plugins{
    id("org.jetbrains.intellij") version "0.4.9"
    kotlin("jvm") version "1.3.41"
    id("se.patrikerdes.use-latest-versions") version "0.2.11"
    id("com.github.ben-manes.versions") version "0.21.0"
}
intellij{
    updateSinceUntilBuild=false
    instrumentCode=true
    version="2019.3"
}

group="com.nurflugel"
version="0.1.0"

repositories{
    jcenter()
    mavenCentral()
}

dependencies{
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.70")
    implementation("org.apache.commons:commons-lang3:3.9")
    testImplementation ("io.kotlintest:kotlintest-runner-junit5:3.2.1")
    testImplementation ("io.mockk:mockk:1.9.1")
}