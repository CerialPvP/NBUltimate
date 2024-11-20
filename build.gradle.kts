
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    //id("com.gradleup.shadow") version "8.3.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("maven-publish")
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

}

// Required for proper command argument names
tasks.compileJava {
    options.compilerArgs.add("-parameters")
    options.isFork = true
    // Adds compatibility with Windows and Mac/Linux
    val sep = File.separator
    options.forkOptions.executable = System.getProperty("java.home")+sep+"bin"+sep+"javac"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("xyz.xenondevs.invui:invui:1.37")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("net.raphimc:NoteBlockLib:2.1.3-SNAPSHOT")
    implementation("io.github.classgraph:classgraph:4.8.176")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<Jar> {
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot"
    }
}

tasks.withType<RunServer> {
    minecraftVersion("1.21.1")
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
    bootstrapper = "cc.cerial.nbultimate.CommandBootstrap"
    apiVersion = "1.21"
    website = "https://github.com/CerialPvP/NBUltimate"
    authors = listOf("oCerial")
    contributors = listOf("RK_01 / RaphiMC")
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