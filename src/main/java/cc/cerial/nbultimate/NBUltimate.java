package cc.cerial.nbultimate;

import cc.cerial.nbultimate.managers.PluginConfig;
import cc.cerial.nbultimate.managers.SongCacheManager;
import cc.cerial.nbultimate.managers.SongPlayerStorageManager;
import cc.cerial.nbultimate.utils.Version;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The main plugin class.
 */
public final class NBUltimate extends JavaPlugin {
    private static NBUltimate instance;
    private static final SongCacheManager songCacheManager = new SongCacheManager();
    private static PluginConfig pluginConfig;
    private static SongPlayerStorageManager songPlayerStorageManager;
    private static Version mcVersion;

    /**
     * @return The plugin's instance.
     * @throws IllegalStateException Thrown if the method is called when the plugin is not loaded.
     */
    public static NBUltimate get() throws IllegalStateException {
        if (instance == null) throw new IllegalStateException("The instance of the plugin is not set. Call this method when the plugin is initialized.");
        return instance;
    }

    /**
     * @return The Song Cache Manager, which is responsible for cached songs.
     * @see SongCacheManager
     */
    public static SongCacheManager getSongCacheManager() {
        return songCacheManager;
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
        ZipFile f = null;
        try {
            f = new ZipFile(this.getFile());
            Enumeration<? extends ZipEntry> entries = f.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.getName().startsWith("songs/") && !entry.getName().equalsIgnoreCase("README.txt")) continue;

                File saveTo = new File(getDataFolder(), entry.getName());

                if (entry.isDirectory()) {
                    if (!saveTo.mkdirs()) getLogger().warning("Couldn't make directory "+saveTo+".");
                    continue;
                }

                try (InputStream is = f.getInputStream(entry); FileOutputStream fos = new FileOutputStream(saveTo)) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                    }
                }
            }
            getLogger().info("Successfully generated all default songs.");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "There was an error while generating files:", e);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException e) {
                    getLogger().log(Level.SEVERE, "There was an error when closing the ZipFile:", e);
                }
            }

        }
    }

    public void registerCommands() {
//        if (imperat != null) {
//            imperat.unregisterAllCommands();
//        } else {
//            imperat = BukkitImperat.builder(this)
//                    .build();
//            imperat.applyBrigadier();
//        }
//
//        // Use ClassGraph to get all classes in commands package which implement OrphanCommand
//        try (ScanResult scanResult = new ClassGraph()
//                .enableAllInfo()
//                .addClassLoader(getClassLoader())
//                .acceptPackages("cc.cerial.nbultimate.commands")
//                .scan()) {
//            for (ClassInfo info: scanResult.getClassesWithAnnotation(Command.class)) {
//                try {
//                    Class<?> clazz = info.loadClass();
//                    imperat.registerCommand(clazz.getDeclaredConstructor().newInstance());
//                    commandRegistered(clazz);
//                } catch (InvocationTargetException | InstantiationException |
//                         IllegalAccessException | NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
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

    @SuppressWarnings({"UnstableApiUsage"})
    @Override
    public void onEnable() {
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

        if (mcVersion.isSmallerThan(new Version(1, 20))) {
            getLogger().warning("You are running a server with version below 1.20.");
            getLogger().warning("NBUltimate does not support servers below 1.20, therefore you are on your own.");
        }

        InvUI.getInstance().setPlugin(this);
        getLogger().info("Enabled InvUI framework.");

        getLogger().info("Versions:");
        getLogger().info("- NoteBlockLib Version: "+LibraryLoader.NOTEBLOCKLIB.getArtifact().getVersion());
        getLogger().info("- SimpleYaml Version: "+LibraryLoader.SIMPLE_YAML.getArtifact().getVersion());
        getLogger().info("- ClassGraph Version: "+LibraryLoader.CLASSGRAPH.getArtifact().getVersion());
        getLogger().info("- InvUI Version: "+LibraryLoader.INVUI.getArtifact().getVersion());

        if (!getDataFolder().exists()) {
            getLogger().info("Adding demo songs...");
            makeSongFolder();
        }

        getLogger().info("Loading configuration...");
        loadConfig();

        getLogger().info("Loading all songs, you might experience lag...");
        songCacheManager.cacheAllSongs();

        getLogger().info("Loading events...");
        registerEvents();

        getLogger().info("Plugin done loading.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabling. Goodbye!");
        pluginConfig = null;
        instance = null;
    }
}