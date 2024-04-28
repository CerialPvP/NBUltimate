package cc.cerial.nbultimate;

import cc.cerial.nbultimate.configuration.MainConfig;
import cc.cerial.nbultimate.gui.regions.RegionAddIcon;
import cc.cerial.nbultimate.noteblock.BaseSongPlayer;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import me.lucko.helper.maven.MavenLibrary;
import me.lucko.helper.maven.Repository;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.exception.CommandErrorException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@MavenLibrary(groupId = "net.raphimc", artifactId = "NoteBlockLib", version = "2.0.6")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-api", version = "4.16.0")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-platform-bukkit", version = "4.3.2")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-text-minimessage", version = "4.16.0")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-text-serializer-gson", version = "4.16.0")
@MavenLibrary(groupId = "com.github.SkriptLang", artifactId = "Skript", version = "2.8.4", repo = @Repository(url = "https://repo.skriptlang.org/releases"))
@MavenLibrary(groupId = "xyz.xenondevs.invui", artifactId = "inventory-access-r18", version = "1.27", repo = @Repository(url = "https://repo.xenondevs.xyz/releases"))
@MavenLibrary(groupId = "org.reflections", artifactId = "reflections", version = "0.10.2")
@MavenLibrary(groupId = "de.exlll", artifactId = "configlib-core", version = "4.5.0")
@MavenLibrary(groupId = "de.exlll", artifactId = "configlib-paper", version = "4.5.0")
public final class NBUltimate extends ExtendedJavaPlugin {

    private static NBUltimate instance = null;
    public static NBUltimate getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The instance of NBUltimate is null!");
        }
        return instance;
    }

    private static BukkitAudiences adventure = null;
    public static BukkitAudiences getAdventure() {
        if (adventure == null) {
            throw new IllegalStateException("The Adventure instance of NBUltimate is null!");
        }
        return adventure;
    }

    private static MainConfig mainConfig;
    public static MainConfig getMainConfig() {
        if (mainConfig == null) {
            throw new IllegalStateException("The main config instance is null!");
        }
        return mainConfig;
    }

    public static Map<BaseSongPlayer, Object> songPlayers = new HashMap<>();

    @Override
    protected void enable() {
        // Enable the plugin, initialize config and enable Adventure
        getLogger().info("Loading plugin...");
        instance = this;
        adventure = BukkitAudiences.create(this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Loading configuration library
        getLogger().info("Loading configuration...");
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .header("""
                        $$\\   $$\\ $$$$$$$\\  $$\\   $$\\ $$\\   $$\\     $$\\                          $$\\
                        $$$\\  $$ |$$  __$$\\ $$ |  $$ |$$ |  $$ |    \\__|                         $$ |
                        $$$$\\ $$ |$$ |  $$ |$$ |  $$ |$$ |$$$$$$\\   $$\\ $$$$$$\\$$$$\\   $$$$$$\\ $$$$$$\\    $$$$$$\\
                        $$ $$\\$$ |$$$$$$$\\ |$$ |  $$ |$$ |\\_$$  _|  $$ |$$  _$$  _$$\\  \\____$$\\\\_$$  _|  $$  __$$\\
                        $$ \\$$$$ |$$  __$$\\ $$ |  $$ |$$ |  $$ |    $$ |$$ / $$ / $$ | $$$$$$$ | $$ |    $$$$$$$$ |
                        $$ |\\$$$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$\\ $$ |$$ | $$ | $$ |$$  __$$ | $$ |$$\\ $$   ____|
                        $$ | \\$$ |$$$$$$$  |\\$$$$$$  |$$ |  \\$$$$  |$$ |$$ | $$ | $$ |\\$$$$$$$ | \\$$$$  |\\$$$$$$$\\
                        \\__|  \\__|\\_______/  \\______/ \\__|   \\____/ \\__|\\__| \\__| \\__| \\_______|  \\____/  \\_______|
                        
                        NBUltimate - Made by Cerial for the NoteBlock community with <3.
                        """)
                .build();

        mainConfig = YamlConfigurations.update(
                new File(getDataFolder(), "config.yml").toPath(),
                MainConfig.class,
                properties
        );

        // Load lamp library
        getLogger().info("Loading lamp library...");
        BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        // Register a suggestion type which returns all songs in the NBUltimate directory
        handler.getAutoCompleter().registerSuggestion("songs",
                (args, sender, commands) -> {
                    List<String> songs = Utils.getAllFiles("plugins/NBUltimate/songs/");
                    return Objects.requireNonNullElseGet(songs, () -> List.of(""));
                });

        handler.setSwitchPrefix("--");

        handler.setHelpWriter((command, actor) -> MiniMessage.miniMessage().deserialize("<gray>- <#ed8b40>/"+command.getPath().toRealString()+" "+command.getUsage()+" <white>| <yellow>"+command.getDescription()));

        handler.registerParameterValidator(String.class, (value, paramater, actor) -> {
            if (!paramater.hasAnnotation(NBSong.class)) return;

            List<String> songs = Utils.getAllFiles("plugins/NBUltimate/songs/");
            if (!songs.contains(value)) {
                throw new CommandErrorException("Invalid song path provided! Provided: "+value);
            }
        });

        getLogger().info("Registering commands...");
        // Register commands
        for (Class<?> clazz: new Reflections("cc.cerial.nbultimate.commands", new SubTypesScanner(false))
                .getSubTypesOf(Object.class)) {
            try {
                handler.register(clazz.getConstructor().newInstance());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        getLogger().info("Registering events...");
        // Register events
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new RegionAddIcon(), this);


        handler.registerBrigadier();
    }

    @Override
    protected void disable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        mainConfig = null;
        instance = null;
    }
}
