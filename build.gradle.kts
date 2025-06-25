plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.rollczi"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")

    testImplementation("org.awaitility:awaitility:4.3.0")
    testImplementation("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.2")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.2")
}

publishing {
    java {
        withSourcesJar()
        withJavadocJar()
    }

    repositories {
        mavenLocal()

        maven(
            name = "eternalcode",
            url = "https://repo.eternalcode.pl",
            username = "ETERNAL_CODE_MAVEN_USERNAME",
            password = "ETERNAL_CODE_MAVEN_PASSWORD",
            snapshots = true,
            beta = true,
        )
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "liteskullapi"
            from(project.components["java"])
        }
    }
}

fun RepositoryHandler.maven(
    name: String,
    url: String,
    username: String,
    password: String,
    snapshots: Boolean = true,
    beta: Boolean = false
) {
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")

    if (isSnapshot && !snapshots) {
        return
    }

    val isBeta = version.toString().contains("-BETA")

    if (isBeta && !beta) {
        return
    }

    this.maven {
        this.name =
            if (isSnapshot) "${name}Snapshots"
            else "${name}Releases"

        this.url =
            if (isSnapshot) uri("$url/snapshots")
            else uri("$url/releases")

        this.credentials {
            this.username = System.getenv(username)
            this.password = System.getenv(password)
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    maxParallelForks = Runtime.getRuntime().availableProcessors()

    tasks.withType<JavaCompile>().configureEach {
        options.isFork = true
        options.compilerArgs.add("-parameters")
    }
}