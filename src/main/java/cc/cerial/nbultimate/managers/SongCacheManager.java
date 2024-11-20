package cc.cerial.nbultimate.managers;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.utils.SongFileUtils;
import cc.cerial.nbultimate.utils.Utils;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.model.Song;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static cc.cerial.nbultimate.utils.Utils.format;

/**
 * Class responsible for managing cached songs. To retrieve an instance of this class used by NBUltimate, use {@link NBUltimate#getSongCacheManager()}.
 */
public class SongCacheManager {
    private final Map<File, Song<?,?,?>> cachedSongs = new HashMap<>();

    public Map<File, Song<?,?,?>> getCachedSongs() {
        return this.cachedSongs;
    }

    /**
     * Add songs in the specified directory to the songs cache.
     * @param dir The directory name you want to cache. For example: {@code "/"} is the root directory of the songs folder (plugins/NBUltimate/songs),
     *            and {@code "/testing"} is plugins/NBUltimate/songs/testing.
     */
    public void cacheSongsInDir(String dir) {
        if (!dir.startsWith("/")) dir = "/" + dir;
        File songs = new File(NBUltimate.get().getDataFolder(), "songs"+dir);

        File[] files = songs.listFiles();
        if (files == null) {
            Utils.sendToOps(format("<red>%s No songs found in directory %s.</red>", Utils.getIcon(Utils.IconTypes.ERROR), songs));
            return;
        }

        Utils.sendToOps(format("<yellow>%s Caching all songs in directory %s...</yellow>", Utils.getIcon(Utils.IconTypes.WAIT), dir));

        for (File loopFile: files) {
            // If the loop file is a directory, cache that directory.
            if (loopFile.isDirectory()) {
                cacheSongsInDir(loopFile.getPath().replaceAll("plugins/NBUltimate/songs", ""));
                continue;
            }

            // If file is (somehow) not a file (impossible cuz of check above), and (is not a song file or disabled) continue.
            if (!loopFile.isFile() && (!SongFileUtils.isSongFile(loopFile.getName()) || SongFileUtils.isDisabled(loopFile.getName()))) continue;
            cachedSongs.remove(loopFile); // Prevent duplicate entries
            try {
                long time = System.currentTimeMillis();
                Song<?,?,?> song = NoteBlockLib.readSong(loopFile);
                cachedSongs.put(loopFile, song);
                Utils.sendToOps(format("<green>%s Added song named %s (%sms)</green>",
                        Utils.getIcon(Utils.IconTypes.SUCCESS), song.getView().getTitle(), System.currentTimeMillis()-time));
            } catch (Exception e) {
                NBUltimate.get().getLogger().log(Level.SEVERE, "Couldn't parse song from file named "+loopFile+":", e);
            }
        }
    }

    /**
     * Caches all songs, starting from the root songs directory (plugins/NBUltimate/songs) and proceed recursively.
     */
    public void cacheAllSongs() {
        cacheSongsInDir("/");
    }
}
