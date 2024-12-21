import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    //id("com.gradleup.shadow") version "8.3.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.3"
}

group = "cc.cerial.nbultimate"
version = "0.0.0-DEV"
description = "The ultimate note blocks plugin/library, made for the Note Block community."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://maven.lenni0451.net/snapshots")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.codemc.org/repository/maven-public/")

}

// Required for proper command argument names
tasks.compileJava {
    options.compilerArgs.add("-parameters")
    options.isFork = true
    // Adds compatibility with Windows and Mac/Linux
    val sep = File.separator
    options.forkOptions.executable = System.getProperty("java.home")+sep+"bin"+sep+"javac"
}

/**
 * Note for those going through this repo:
 * I am doing a no-shading approach, but CommandAPI either needs to be shaded or
 * be downloaded as a plugin (which I don't want, as this is a public plugin).
 * All other libraries are loaded in the LibraryLoader class.
 */
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    compileOnly("net.raphimc:NoteBlockLib:2.1.3-SNAPSHOT")
    compileOnly("io.github.classgraph:classgraph:4.8.176")
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")

    // InvUI
    implementation("xyz.xenondevs.invui:invui-core:1.43")
    implementation("xyz.xenondevs.invui:inventory-access-r22:1.43") // 1.21.4
    implementation("xyz.xenondevs.invui:inventory-access-r21:1.43") // 1.21.2
    implementation("xyz.xenondevs.invui:inventory-access-r20:1.43") // 1.21
    implementation("xyz.xenondevs.invui:inventory-access-r19:1.43") // 1.20.5

}

tasks.withType<Jar> {
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot"
    }
}

tasks.withType<ShadowJar> {
    // Only relocate when not running test server.
    if (!gradle.startParameter.taskNames.contains("runServer"))
        relocate("dev.jorel.commandapi", "cc.cerial.nbultimate.commandapi")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<RunServer> {
    minecraftVersion("1.21.3")
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        @Suppress("UnstableApiUsage")
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-Xms768M", "-Xmx1536M", "-XX:+AllowEnhancedClassRedefinition", "-XX:+UseG1GC",
        "-XX:+ParallelRefProcEnabled", "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions",
        "-XX:+DisableExplicitGC", "-XX:+AlwaysPreTouch", "-XX:G1HeapWastePercent=5", "-XX:G1MixedGCCountTarget=4",
        "-XX:InitiatingHeapOccupancyPercent=15", "-XX:G1MixedGCLiveThresholdPercent=90",
        "-XX:G1RSetUpdatingPauseTimePercent=5", "-XX:SurvivorRatio=32", "-XX:+PerfDisableSharedMem",
        "-XX:MaxTenuringThreshold=1", "-Dusing.aikars.flags=https://mcflags.emc.gs", "-Daikars.new.flags=true",
        "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40", "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20")
}

paper {
    main = "cc.cerial.nbultimate.NBUltimate"
    loader = "cc.cerial.nbultimate.LibraryLoader"
    //bootstrapper = "cc.cerial.nbultimate.CommandBootstrap"
    apiVersion = "1.21.3"
    website = "https://github.com/CerialPvP/NBUltimate"
    authors = listOf("oCerial")
    contributors = listOf("RK_01 / RaphiMC")
    permissions {
        register("nbultimate.play") {
            description = "Allows the user with the permission to use /nb play."
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("nbultimate.reload") {
            description = "Allows the user with the permission to use /nb reload."
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("nbultimate.download") {
            description = "Allows the user with the permission to view information about downloaded assets."
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

publishing {
    repositories {
        maven {
            name = "cerialrepo"
            url = uri("http://45.137.70.207:25502/snapshots")
            isAllowInsecureProtocol = true
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "cc.cerial"
            artifactId = "NBUltimate"
            version = project.version.toString()
            from(components["java"])
        }
    }
}