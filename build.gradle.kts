plugins {
    id("java-library")
    id("fabric-loom") version "1.16-SNAPSHOT"
    id("com.gradleup.shadow") version "9.4.1"
}

group = "com.coloryr"
version = "4.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

val shadowImpl by configurations.creating
configurations.named("shadow") { extendsFrom(shadowImpl) }
configurations.named("implementation") { extendsFrom(shadowImpl) }

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.10")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.115.1+1.21.4")

    shadowImpl("org.apache.httpcomponents.client5:httpclient5:5.6.1")
    shadowImpl("org.apache.httpcomponents.core5:httpcore5:5.4.2")
    shadowImpl("org.apache.httpcomponents.core5:httpcore5-h2:5.4.2")
    compileOnly("com.google.code.gson:gson:2.14.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.25.4")
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        configurations = listOf(project.configurations.shadow.get())
        mergeServiceFiles()
        isZip64 = true
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("[fabric-1.21.4]AllMusic_Client-${project.version}.jar")
        isZip64 = true
    }

    build {
        dependsOn(remapJar)
    }
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}
