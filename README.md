# NBUltimate
Hello, and thank you for visiting the NBUltimate GitHub repository.<br>
The source code for NBUltimate is hosted here.
- [What is this?](#what-is-this)
- [Downloading the plugin](#downloading-the-plugin)
- [For developers](#for-developers)
  - [Importing NBUltimate to your build tool](#importing-nbultimate-to-your-build-tool)
  - [Adding NBUltimate as a dependency in your plugin](#adding-nbultimate-as-a-dependency-in-your-plugin)
  - [Retrieving an instance of NBUltimate](#retrieving-an-instance-of-nbultimate)
  - [Building NBUltimate](#building-nbultimate)
  - [Running a testing server](#running-a-testing-server)
## What is this?
This is a [PaperMC](https://papermc.io) plugin, that allows you to playback
and manipulate NBS files, and convert the following formats to NBS:
- .mid ([MIDI](https://en.wikipedia.org/wiki/MIDI) format)
- .mcsp2 ([Minecraft Song Planner 2](https://web.archive.org/web/20110923175931/http://www.minecraftforum.net/topic/136749-minecraft-song-planner-25-a-tool-for-note-block-musicians/)'s format)
- .notebot ([Future Client](https://futureclient.net/)'s format)
- .txt ([BleachHack](https://github.com/BleachDev/BleachHack)'s format)
## Downloading the plugin
> **:warning: HUGE WARNING: NBUltimate REQUIRES for you to use a Paper server (or a fork). Spigot will 100% not work, and ignore
the plugin (since this is a Paper plugin), and "hybrid" forks (such as Arclight) aren't guaranteed to work.**
### Dev Build
> **:warning: WARNING: A GitHub account is required for accessing Actions.**
1. Head over to **Actions** tab (as mentioned above, a GitHub account is required.)
2. Go to latest workflow run.
3. Download the artifact, and extract the ZIP file.
4. Put the extracted JAR file in your server's plugins folder.
5. Reboot the server and let the plugin download all its required libraries.
### Release Build
1. Click on the latest release, or head over to https://github.com/CerialPvP/NBUltimate/releases/latest
2. Download the NBUltimate jar file.
3. Put the downloaded JAR file in your server's plugins folder.
4. Reboot the server and let the plugin download all its required libraries.
## For developers
### Importing NBUltimate to your build tool
> **:warning: WARNING: If you are using a shading plugin, ADD NBULTIMATE TO YOUR SHADING EXCLUSIONS LIST.**

To add NBUltimate in Maven:
```xml
<repositories>
    <repository>
        <id>emirdev</id>
        <url>https://repo.emirdev.xyz/snapshots</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>cc.cerial</groupId>
        <artifactId>NBUltimate</artifactId>
        <version>[REPLACE VERSION HERE]</version>
    </dependency>
</dependencies>
```
To add NBUltimate in Gradle Groovy:
```groovy
repositories {
    maven {
        url "https://repo.emirdev.xyz/snapshots"
    }
}
```
```groovy
dependencies {
    implementation "cc.cerial:NBUltimate:0.0.0-DEV"
}
```
To add NBUltimate in Gradle KTS:
```kt
repositories {
    maven("https://repo.emirdev.xyz/snapshots")
}
```
```kt
dependencies {
    implementation("cc.cerial:NBUltimate:0.0.0-DEV")
}
```
### Adding NBUltimate as a dependency in your plugin
> :warning:  **WARNING: If you declare NBUltimate as a soft-dependency (your plugin won't require NBUltimate to start up), make sure
to add a field in your plugin to signal that NBUltimate is present and enabled, like this:**
```java
public class YourPlugin extends JavaPlugin {
    private boolean isNBUltimateEnabled = false;
    // Getter method here..
    
    @Override
    public void onEnable() {
        // Plugin startup logic here...
        PluginManager pm = getServer().getPluginManager();
        if (pm.isPluginEnabled(pm.getPlugin("NBUltimate"))) this.isNBUltimateEnabled = true;
    }
}
```
bukkit.yml:
```yaml
# Plugin information goes here.
# If you want your plugin to start up only if NBUltimate is present, use "depend" list, otherwise use "softdepend", and
# put the code in the section above to check if NBUltimate is present on your server.
depend:
  - NBUltimate
  # Other dependencies
```
paper-plugin.yml:
```yaml
dependencies:
  server:
    NBUltimate:
      # NBUltimate should be loaded before your plugin is, to avoid errors.
      load: BEFORE
      # If this is true, NBUltimate is required for your plugin to start up, otherwise NBUltimate will be a soft-dependency
      # and you will need to use the code above to check if NBUltimate is present.
      required: true
      # This should be true, otherwise your plugin will get ClassNotFoundException errors.
      join-classpath: true
```
### Building NBUltimate
Run the `clean` and `build` task in Gradle.<br>
On Windows:
```
gradlew.bat clean build
```
On Mac/Linux:
```shell
./gradlew clean build
```
### Running a testing server
> **:warning: WARNING: When running the server, you automatically agree to the Mojang EULA. The server is only to be used with NBUltimate, and not with other plugins.**

NBUltimate also offers the option of running a dedicated test server from your IDE.
Simply run the `runServer``<br>
On Windows:
```
gradlew.bat runServer
```
On Mac/Linux:
```shell
./gradlew runServer
```