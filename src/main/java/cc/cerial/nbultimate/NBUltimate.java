package cc.cerial.nbultimate;

import cc.cerial.nbultimate.guis.buttons.BackItem;
import cc.cerial.nbultimate.guis.buttons.ForwardItem;
import cc.cerial.nbultimate.managers.PluginConfig;
import cc.cerial.nbultimate.managers.SongCacheManager;
import cc.cerial.nbultimate.managers.SongPlayerStorageManager;
import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import cc.cerial.nbultimate.utils.MathUtils;
import cc.cerial.nbultimate.utils.ResourcePackDownloader;
import cc.cerial.nbultimate.utils.Utils;
import cc.cerial.nbultimate.utils.Version;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.ItemWrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main plugin class.
 */
public final class NBUltimate extends JavaPlugin {
    private static NBUltimate instance;
    private static final SongCacheManager songCacheManager = new SongCacheManager();
    private static PluginConfig pluginConfig;
    private static SongPlayerStorageManager songPlayerStorageManager;
    private static Version mcVersion;
    private static ResourcePackDownloader packDownloader;
    private static Structure guiStructure;

    /**
     * @return The plugin's instance.
     * @throws IllegalStateException Thrown if the method is called when the plugin is not loaded.
     */
    public static NBUltimate get() throws IllegalStateException {
        if (instance == null) throw new IllegalStateException("The instance of the plugin is not set. Call this method when the plugin is initialized.");
        return instance;
    }

    /**
     * @return The Resource Pack Downloader used for this plugin.
     * @see ResourcePackDownloader
     */
    public static ResourcePackDownloader getPackDownloader() {
        return packDownloader;
    }

    /**
     * @return The Song Cache Manager, which is responsible for cached songs.
     * @see SongCacheManager
     */
    public static SongCacheManager getSongCacheManager() {
        return songCacheManager;
    }

    @ApiStatus.Internal
    public static Structure getGuiStructure() {
        return guiStructure;
    }

    /**
     * @return The PluginConfig instance used by this plugin.
     * @see PluginConfig
     */
    public static PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public static SongPlayerStorageManager getSongPlayerStorageManager() {
        return songPlayerStorageManager;
    }

    public static void loadConfig() {
        if (pluginConfig != null) pluginConfig = null;
        pluginConfig = new PluginConfig();
    }

    public static void updateMCVersion() {
        String ver = Bukkit.getBukkitVersion();
        Matcher m = Pattern
                .compile("\\d+\\.\\d+(\\.\\d+)?")
                .matcher(ver);
        if (!m.find()) {
            mcVersion = new Version(666, 0, 0);
        } else {
            mcVersion = new Version(m.group());
        }
    }

    private void makeSongFolder() {
        if (!getDataFolder().mkdirs()) getLogger().severe("Couldn't make NBUltimate folder.");
        Utils.unzip(this.getFile());
    }

    public void registerEvents() {
        // Unregister listeners in case this is called from /nb reload
        HandlerList.unregisterAll(this);

        // Register all events
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .addClassLoader(getClassLoader())
                .acceptPackages("cc.cerial.nbultimate.events")
                .scan()) {
            for (ClassInfo info: scanResult.getClassesImplementing("org.bukkit.event.Listener")) {
                try {
                    Class<?> clazz = info.loadClass();
                    getServer().getPluginManager().registerEvents((Listener) clazz.getDeclaredConstructor().newInstance(), this);
                    getLogger().info("Registered event from class "+clazz.getSimpleName()+" successfully.");
                } catch (InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void registerCommandAPI(boolean reload) {
        // If reloading, we unload the /nb command and CommandAPI.
        if (reload) {
            CommandAPI.unregister("nb");
            CommandAPI.onDisable();
        }

        CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this)
                .verboseOutput(true)
                .usePluginNamespace()
                .shouldHookPaperReload(true);
        CommandAPI.onLoad(config);

        CommandAPICommand root = new CommandAPICommand("nb")
                .withAliases("noteblock")
                .withFullDescription("Root command for NBUltimate.");

        getLogger().info("Regisering commands...");
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .addClassLoader(this.getClassLoader())
                .acceptPackages("cc.cerial.nbultimate.commands")
                .scan()) {
            for (ClassInfo info: scanResult.getAllClasses()) {
                try {
                    AbstractCommand command = (AbstractCommand) info.loadClass().getConstructor().newInstance();
                    root.withSubcommand(command.getCommandData());
                    getLogger().info("Registered subcommand "+command.getCommandData().getName()+" from class "+info.getSimpleName());
                } catch (InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        root.register();
        if (reload) {
            CommandAPI.onEnable();
        }
    }

    @Override
    public void onLoad() {
        registerCommandAPI(false);
    }

    @SuppressWarnings({"UnstableApiUsage"})
    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        instance = this;
        songPlayerStorageManager = new SongPlayerStorageManager();
        updateMCVersion();

        getLogger().info("Starting up NBUltimate...");
        if (!new Version(getPluginMeta().getVersion()).isStable()) {
            getLogger().warning("You are running a non-released version of NBUltimate.");
            getLogger().warning("If you experience bugs, report them in one of the following sources:");
            getLogger().warning("- Discord: https://discord.gg/8EX7SfMdGG -> Bug reports channel");
            getLogger().warning("- GitHub: https://github.com/CerialPvP/NBUltimate -> Issues");
        }

        if (mcVersion.isSmallerThan(new Version(1, 20, 5))) {
            getLogger().warning("You are running a server with version below 1.20.5.");
            getLogger().warning("NBUltimate does not support servers below 1.20.5, therefore you are on your own.");
        }


        getLogger().info("Versions:");
        getLogger().info("- Minecraft Server Version: "+mcVersion);
        getLogger().info("- NoteBlockLib Version: "+LibraryLoader.NOTEBLOCKLIB.getArtifact().getVersion());
        getLogger().info("- SimpleYaml Version: "+LibraryLoader.SIMPLE_YAML.getArtifact().getVersion());
        getLogger().info("- ClassGraph Version: "+LibraryLoader.CLASSGRAPH.getArtifact().getVersion());

        InvUI.getInstance().setPlugin(this);
        // GLASS PANES (capital letters)
        Structure.addGlobalIngredient('A', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.RED_STAINED_GLASS_PANE))));
        Structure.addGlobalIngredient('B', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE))));
        Structure.addGlobalIngredient('C', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE))));
        Structure.addGlobalIngredient('D', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.BROWN_STAINED_GLASS_PANE))));
        // Regular glass (lower case letters)
        Structure.addGlobalIngredient('a', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.RED_STAINED_GLASS))));
        Structure.addGlobalIngredient('b', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.ORANGE_STAINED_GLASS))));
        Structure.addGlobalIngredient('c', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.YELLOW_STAINED_GLASS))));
        Structure.addGlobalIngredient('d', new ItemWrapper(Utils.hideTooltip(new ItemStack(Material.BROWN_STAINED_GLASS))));
        // Pagination
        Structure.addGlobalIngredient('#', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        Structure.addGlobalIngredient('<', BackItem::new);
        Structure.addGlobalIngredient('>', ForwardItem::new);

        guiStructure = new Structure(
                "a A B C D A B C b",
                "A # # # # # # # A",
                "B # # # # # # # B",
                "C # # # # # # # C",
                "D # # # # # # # D",
                "c A B < D > B C d"
        );
        getLogger().info("Enabled InvUI framework.");

        if (!getDataFolder().exists()) {
            getLogger().info("Adding demo songs...");
            makeSongFolder();
        }

        getLogger().info("Loading configuration...");
        loadConfig();

//        getLogger().info("Downloading resource packs...");
//        packDownloader = new ResourcePackDownloader.Builder()
//                .minecraftPack(mcVersion)
//                .customAssets(pluginConfig.getCustomPacks().values().stream().map(Object::toString).toList())
//                .build();
//
//        packDownloader.download();

        getLogger().info("Precalculating sin/cos values...");
        MathUtils.precalculateSinCos();

        getLogger().info("Loading all songs, you might experience lag...");
        songCacheManager.cacheAllSongs();

        getLogger().info("Loading events...");
        registerEvents();

        getLogger().info("Plugin done loading.");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        getLogger().info("Plugin disabling. Goodbye!");
        pluginConfig = null;
        instance = null;
    }
}