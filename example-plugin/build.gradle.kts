import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.9"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.rollczi"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.9-R0.1-SNAPSHOT")
    implementation(rootProject.project)

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

bukkit {
    main = "dev.rollczi.skullplugin.SkullPlugin"
    apiVersion = "1.13"
    author = "Rollczi"
    name = "LiteSkullApiTest"
    version = "${project.version}"
    commands.create("give-skull")
}

tasks.runServer {
    minecraftVersion("1.21.5")
    allJvmArgs = listOf("-DPaper.IgnoreJavaVersion=true")
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("LiteSkullAPIPlugin v${project.version} (MC 1.8.8-1.21x).jar")

    val prefix = "dev.rollczi.skullplugin.libs"
    listOf(
        "dev.rollczi.liteskullapi",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
