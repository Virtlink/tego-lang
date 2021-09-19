//plugins {
//    id("org.metaborg.gradle.config.java-library") version "0.4.5"
//    id("org.metaborg.gradle.config.junit-testing") version "0.4.5"
//}
plugins {
    java
}

group = "mb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://artifacts.metaborg.org/content/groups/public/")
}

dependencies {
    implementation("org.metaborg:pie.api:0.16.0")
    implementation("org.metaborg:org.spoofax.terms:2.6.0-SNAPSHOT")

    compileOnly("org.checkerframework:checker-qual-android")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}