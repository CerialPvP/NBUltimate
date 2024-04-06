package cc.cerial.nbultimate;

import cc.cerial.nbultimate.commands.HelpCommand;
import cc.cerial.nbultimate.commands.PlayCommand;
import cc.cerial.nbultimate.commands.ReloadCommand;
import me.lucko.helper.maven.MavenLibrary;
import me.lucko.helper.maven.Repository;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.exception.CommandErrorException;

import java.util.List;

@MavenLibrary(groupId = "net.raphimc", artifactId = "NoteBlockLib", version = "2.0.6")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-api", version = "4.16.0")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-platform-bukkit", version = "4.3.2")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-text-minimessage", version = "4.16.0")
@MavenLibrary(groupId = "net.kyori", artifactId = "adventure-text-serializer-legacy", version = "4.16.0")
//@MavenLibrary(groupId = "xyz.xenondevs.invui", artifactId = "invui", version = "1.27", repo = @Repository(url = "https://repo.xenondevs.xyz/releases"))
@MavenLibrary(groupId = "com.sk89q.worldguard", artifactId = "worldguard-bukkit", version = "7.1.0-SNAPSHOT", repo = @Repository(url = "https://maven.enginehub.org/repo/"))
@MavenLibrary(groupId = "com.github.SkriptLang", artifactId = "Skript", version = "2.8.4", repo = @Repository(url = "https://repo.skriptlang.org/releases"))
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

    @Override
    protected void enable() {
        // Enable the plugin, initialize config and enable Adventure
        instance = this;
        adventure = BukkitAudiences.create(this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Hook into WorldGuard
        if (
                getConfig().getBoolean("worldguard") &&
                getServer().getPluginManager().getPlugin("WorldGuard") != null
        ) {

        }

        BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        // Register a suggestion type which returns all songs in the NBUltimate directory
        handler.getAutoCompleter().registerSuggestion("songs",
                (args, sender, commands) -> Utils.getAllFiles("plugins/NBUltimate/songs/"));

        handler.setHelpWriter((command, actor) -> MiniMessage.miniMessage().deserialize("<gray>- <#ed8b40>/"+command.getPath().toRealString()+" "+command.getUsage()+" <white>| <yellow>"+command.getDescription()));

        handler.registerParameterValidator(String.class, (value, paramater, actor) -> {
            if (!paramater.hasAnnotation(NBSong.class)) return;

            List<String> songs = Utils.getAllFiles("plugins/NBUltimate/songs/");
            if (!songs.contains(value)) {
                throw new CommandErrorException("Invalid song path provided! Provided: "+value);
            }
        });

        handler.register(new PlayCommand());
        handler.register(new HelpCommand());
        handler.register(new ReloadCommand());
        handler.registerBrigadier();
    }

    @Override
    protected void disable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        instance = null;
    }
}
