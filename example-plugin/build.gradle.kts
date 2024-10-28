import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.github.johnrengelman.shadow")
}

group = "dev.rollczi"
version = "1.3.0"

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }

}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.1-R0.1-SNAPSHOT")
    implementation(rootProject.project)

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
}

bukkit {
    main = "dev.rollcz.skullplugin.SkullPlugin"
    apiVersion = "1.13"
    author = "Rollczi"
    name = "LiteSkullApiTest"
    version = "${project.version}"
    commands.create("give-skull")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("LiteSkullAPIPlugin v${project.version} (MC 1.8.8-1.19x).jar")

    exclude("org/intellij/lang/annotations/**","org/jetbrains/annotations/**","org/checkerframework/**","META-INF/**","javax/**")

    mergeServiceFiles()
    minimize()

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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}