import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.jsinco.avatarserver"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.eclipse.jetty:jetty-server:9.0.2.v20130417")
    implementation("org.eclipse.jetty:jetty-io:9.0.2.v20130417")
    implementation("org.eclipse.jetty:jetty-util:9.0.2.v20130417")
    implementation("org.eclipse.jetty:jetty-servlet:9.0.2.v20130417")
    implementation("org.eclipse.jetty:jetty-security:9.0.2.v20130417")
    implementation("org.eclipse.jetty:jetty-http:9.0.2.v20130417")
}

tasks {

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
    }

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
            //include(dependency("org.eclipse.jetty:jetty-server"))
            //include(dependency("org.eclipse.jetty:jetty-webapp"))
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