package cc.cerial.nbultimate.managers;

import cc.cerial.nbultimate.utils.exceptions.InvalidConfigException;
import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.utils.Version;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.text.CompactNumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PluginConfig {
    private static final File CONFIG_FILE = new File(NBUltimate.get().getDataFolder(), "config.yml");
    @SuppressWarnings("UnstableApiUsage")
    private static final Version PLUGIN_VERSION = new Version(NBUltimate.get().getPluginMeta().getVersion());

    private final YamlFile yamlFile;
    private final List<ConfigEntry> entries;

    public List<ConfigEntry> getEntries() {
        return this.entries;
    }

    public PluginConfig() {
        this.yamlFile = new YamlFile(CONFIG_FILE);
        try {
            this.yamlFile.createOrLoadWithComments();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create/load the config file:", e);
        }

        entries = new ArrayList<>();

        String header = """
                d8b   db d8888b. db    db db      d888888b d888888b .88b  d88.  .d8b.  d888888b d88888b\s
                888o  88 88  `8D 88    88 88      `~~88~~'   `88'   88'YbdP`88 d8' `8b `~~88~~' 88'    \s
                88V8o 88 88oooY' 88    88 88         88       88    88  88  88 88ooo88    88    88ooooo\s
                88 V8o88 88~~~b. 88    88 88         88       88    88  88  88 88~~~88    88    88~~~~~\s
                88  V888 88   8D 88b  d88 88booo.    88      .88.   88  88  88 88   88    88    88.    \s
                VP   V8P Y8888P' ~Y8888P' Y88888P    YP    Y888888P YP  YP  YP YP   YP    YP    Y88888P\s
                
                Made by Cerial for the Note Block community with <3
                • Generated at: %s
                
                Small guide for modifying this config:
                • Any configuration value which you see "WARNING" in its comment, only change it if you know what you're doing!
                • If you get any YML parsing errors, put the config file in https://yaml-online-parser.appspot.com/, and check what it tells you.
                • To reload the config, run /nb reload in-game (requires nbultimate.reload permission)
                """;
        this.yamlFile.setHeader(String.format(header, new SimpleDateFormat("E, MMMM dd yyyy h:m:s a z").format(new Date())));

        entries.add(new ConfigEntry(this, "version", String.class, PLUGIN_VERSION.toString(), """
                WARNING: NBUltimate reads and writes this value, so the auto converter can run.
                If you change this value manually, just know it may brick your configuration.
                """));

        /*
         -- DEBUGGING --
         */
        entries.add(new ConfigEntry(this, "debugging.notes", Boolean.class, false, """
                Note Debugging
                • Every time a note is played in any song player, its information will be sent to all players.
                • This setting shouldn't be on in a production environment, as songs with lots of notes
                  can spam people's chat, which isn't only annoying but can also crash people's games.
                
                - Allowed Type: boolean (true/false)
                - Default Value: false"""));

        entries.add(new ConfigEntry(this, "debugging.panning", Boolean.class, false, """
                Panning Debugging
                • Every time a note is played, a dust particle is displayed behind the player, for visualization of panning.
                • This should be used in combination with the Note Debugging feature.

                - Allowed Type: boolean (true/false)
                - Default Value: false"""));


        /*
          -- YAW ROUNDING --
         */

        entries.add(new ConfigEntry(this, "yaw-accuracy", Integer.class, 4, """
                Yaw Accuracy
                • When NBUltimate is loaded, it pre-calculates all sin/cos values from 0 to 360 degrees.
                • Now, NBUltimate doesn't calculate just from 0, 1, 2 until 360, it counts from
                  (assuming this config value is 4) 0.0001, 0.0002, 0.0003 all the way until 360.
                • NBUltimate uses all precalculated sin/cos values in order to calculate the panning.
                • In Bukkit, the player's yaw has a 6 decimal accuracy, which means in order to get
                  as accurate panning as possible, we need to calculate sin/cos values up to 6 decimals
                  (this means we do 360*1,000,000=720 MILLION calculations on startup)
                • If you want to give up some of the accuracy, you can change the accuracy here.
                
                - Allowed Type: Integer (3)
                - Default Value: 4"""));


        /*
         -- PRIORITY --
         */

        entries.add(new ConfigEntry(this, "default-priority", Double.class, 1.0d, """
                Default Priority
                • Every song player without a different priority set will have this priority by default.
                • This value is ignored if a song player has a different priority than the default
                  (e.g. if you made a song player through the API or changed the priority in the song players menu)
                
                - Allowed Type: Double-precision floating number (3.1415926535)
                - Default Value: 1.0"""));

        /*
          -- PANNING SPACING --
         */
        entries.add(new ConfigEntry(this, "panning-spacing", Double.class, 2.2d, """
                Panning Spacing
                • The song's panning is multiplied by this number.
                • For example: Let's say there is a note with panning 0.75, and this config's value is 2, NBUltimate
                               does 0.75 * 2, which is 1.5, so the note will be played 1.5 blocks to the right of
                               the player.
                
                - Allowed Type: Double-precision floating number (3.1415926535)
                - Default Value: 2.2"""));

        /*
          -- EXPERIMENTAL NPS --
         */
        this.yamlFile.setComment("current-notes", """
                Current Notes
                • This adds a "Notes" section to the progress bar, displaying the amount of current played notes.
                • The way this works is like this:
                  • When NBUltimate is going to play a sound, it "simulates" the sound playing.
                  • Basically, there is a list of sounds currently played, and when a sound is played, it gets
                    added to that list, and removed after the length of the sound has passed.
                  • For example, if the current played sound is 500 milliseconds, that sound will stay in the
                    list for 500 milliseconds. Keep in mind if a sound is less than 50 milliseconds, it will be
                    in the list for 50 milliseconds, no less (due to Minecraft not allowing anything below 1 tick).
                • If you have enabled this feature for the first time, you may need to restart your server or
                  run /nb downloadresources, in order to download resource packs.
                • NOTE: Minecraft assets are downloaded from mcasset.cloud, which isn't affiliated
                        with Mojang nor Microsoft. NBUltimate is also not affiliated with Mojang
                        nor Microsoft, the assets are being used for this feature.
                • If you have enabled this feature before, disable the config toggle, and optionally,
                  delete the downloaded resource packs.
                """);

        entries.add(new ConfigEntry(this, "current-notes.enabled", Boolean.class, false, """
                Current Notes - Toggle
                • Enables the Current Notes feature.
                • For more information, look at the comment above.
                
                - Allowed Type: boolean (true/false)
                - Default Value: false"""));

        this.yamlFile.setComment("current-notes.custom-packs", """
                Current Notes - Custom Resource Packs
                • If your server has a resource pack with custom sounds, you should provide them here.
                • The format you provide custom sounds is by providing the namespace of your resource pack
                  and a direct URL to download it.
                • For more information about namespaces, visit the Minecraft Wiki article about it:
                  https://minecraft.wiki/w/Resource_location#Namespaces.
                • If you aren't sure on which namespace your resource pack uses, you can check by unzipping
                  your resource pack, visiting the "assets" folder and checking what folders exist in there.
                  If more than 1 folder exist, go to one of the folders, and check if a "sounds" folder is present.
                  If there isn't a sounds folder, check the other folders. The folder with a sounds folder is
                  the one you should provide to NBUltimate.
                • A quick note: If your resource pack overrides existing Minecraft sounds, NBUltimate will NOT
                  check for those, and instead use the default Minecraft sounds.
                """);

        this.yamlFile.addDefault("current-notes.custom-packs",
                List.of(Map.of("minecraft", "https://github.com/RaphiMC/NoteBlockLib/raw/refs/heads/main/Extended%20Octave%20Range%20Notes%20Pack.zip")));


        validateConfig();
        saveFile();
    }

    public void validateConfig() throws InvalidConfigException {
        List<String> invalid = new ArrayList<>();
        for (ConfigEntry entry: entries) {
            Object value = this.yamlFile.get(entry.getPath());
            if (value.getClass() != entry.getType()) invalid.add(String.format("- %s: Expected type %s, got %s.", entry.getPath(), entry.getType().getSimpleName(), value.getClass().getSimpleName()));
        }
        if (!invalid.isEmpty()) throw new InvalidConfigException(invalid);
    }

    public static class ConfigEntry {
        private final String path;
        private final Class<?> type;
        private final Object value;
        private final String comment;
        private final PluginConfig instance;

        public ConfigEntry(PluginConfig instance, String path, Class<?> type, Object value, String comment) {
            this.path = path;
            this.type = type;
            this.value = value;
            this.comment = comment;
            this.instance = instance;

            instance.yamlFile.setComment(path, comment);
            instance.yamlFile.addDefault(path, value);
        }

        /**
         * @return The path of this entry, for example "something.1".
         */
        public String getPath() {
            return this.path;
        }

        /**
         * @return The type of this entry, for example String.class or Boolean.class.
         */
        public Class<?> getType() {
            return this.type;
        }

        /**
         * @return The default value assigned to this entry.
         */
        public Object getDefaultValue() {
            return this.value;
        }

        /**
         * @return The current value assigned to this entry.
         */
        public Object getCurrentValue() {
            return instance.yamlFile.get(this.path);
        }

        /**
         * @return The comment of this entry.
         */
        public String getComment() {
            return this.comment;
        }
    }

    private void saveFile() {
        try {
            this.yamlFile.save();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save config file:", e);
        }
    }

    public boolean getNoteDebugging() {
        return this.yamlFile.getBoolean("debugging.notes");
    }

    public void setNoteDebugging(boolean debugging) {
        this.yamlFile.set("debugging.notes", debugging);
        saveFile();
    }

    public boolean getPanningDebugging() {
        return this.yamlFile.getBoolean("debugging.panning");
    }

    public void setPanningDebugging(boolean value) {
        this.yamlFile.set("debugging.panning", value);
        saveFile();
    }

    public double getPanningSpacing() {
        return this.yamlFile.getDouble("panning-spacing");
    }

    public Map<String, Object> getCustomPacks() {
        return this.yamlFile.getConfigurationSection("current-notes.custom-packs").getMapValues(false);
    }

    public Version getConfigVersion() {
        return new Version(this.yamlFile.getString("version"));
    }

    public int getYawAccuracy() {
        return this.yamlFile.getInt("yaw-accuracy");
    }
}