plugins {
    kotlin("jvm") version "1.3.61"
    //id("org.jetbrains.dokka")
}

group = "com.snaulX.Tangara"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.8.+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    compile(fileTree("lib") { include("*.jar") })
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    jar {
        archiveFileName.set("Tangara.jar")
        manifest {
            attributes("Main-Class" to "com.snaulX.Tangara.MainProgramKt",
            "Class-Path" to configurations.compileClasspath.name)
        }
    }
    /*val dokka by getting(DokkaTask::class) {
        outputFormat = "md"
        outputDirectory = "docs"
    }*/
}