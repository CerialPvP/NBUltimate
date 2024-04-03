package cc.cerial.nbultimate;

import cc.cerial.nbultimate.commands.HelpCommand;
import cc.cerial.nbultimate.commands.PlayCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.exception.CommandErrorException;

import java.util.List;

public final class NBUltimate extends JavaPlugin {

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
    public void onEnable() {
        // Do default plugin stuff
        instance = this;
        adventure = BukkitAudiences.create(this);
        saveDefaultConfig();

        BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        // Register a suggestion type which returns all songs in the NBUltimate directory
        handler.getAutoCompleter().registerSuggestion("songs",
                (args, sender, commands) -> Utils.getAllFiles("plugins/NBUltimate/songs"));

        handler.setHelpWriter((command, actor) -> String.format("%s %s - %s", command.getPath().toRealString(), command.getUsage(), command.getDescription()));

        handler.registerParameterValidator(String.class, (value, paramater, actor) -> {
            if (!paramater.hasAnnotation(NBSong.class)) return;

            List<String> songs = Utils.getAllFiles("plugins/NBUltimate/songs");
            value = value.replaceAll("plugins/NBUltimate/songs/", "");
            if (!songs.contains(value)) {
                throw new CommandErrorException("Invalid song path provided! Provided: "+value);
            }
        });

        handler.register(new PlayCommand());
        handler.register(new HelpCommand());
        handler.registerBrigadier();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
