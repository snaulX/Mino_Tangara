plugins {
    kotlin("jvm") version "1.3.61"
    //id("org.jetbrains.dokka")
}

group = "com.snaulX.Tangara"
version = "1.0.0"

repositories {
    mavenCentral()
    //jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.8.+")
    compile(fileTree("lib") { include("*.jar") })
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    /*val dokka by getting(DokkaTask::class) {
        outputFormat = "md"
        outputDirectory = "docs"
    }*/
}