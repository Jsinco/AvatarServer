import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.jsinco.avatarserver"
version = "1.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.jsinco.dev/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    implementation("org.eclipse.jetty:jetty-server:9.0.2.v20130417")
    implementation("dev.jsinco.abstractjavafilelib:AbstractJavaFileLib:2.4")
}

tasks {

    assemble {
        dependsOn(shadowJar)
    }

    processResources {
        outputs.upToDateWhen { false }
        filter<ReplaceTokens>(mapOf(
            "tokens" to mapOf("version" to project.version.toString()),
            "beginToken" to "\${",
            "endToken" to "}"
        ))
    }

    shadowJar {
        dependencies {
            // include all
        }
        archiveClassifier.set("")
    }

    jar {
        manifest {
            attributes("Main-Class" to "dev.jsinco.avatarserver.StandaloneServer")
        }
        enabled = false
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}