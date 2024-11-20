package cc.cerial.nbultimate.utils;

import java.util.regex.Pattern;

public class SongFileUtils {
    private static final Pattern SONG_FILE_PATTERN = Pattern.compile("\\.(nbs|mcsp2|mid|txt|notebot)$");

    /**
     * Checks if the provided song file name is a valid song file that can be provided to NoteBlockLib.
     * @param name A song file name you want to check.
     * @return A boolean, determining if the following file is a valid song file.
     */
    public static boolean isSongFile(String name) {
        return SONG_FILE_PATTERN.matcher(name).find();
    }

    /**
     * Checks if the provided song file name is a disabled song (prefixed with a - (dash)), for example -somesong.nbs
     * @param name A song file name you want to check.
     * @return A boolean, determining if the following file is a disabled song file.
     */
    public static boolean isDisabled(String name) {
        return name.startsWith("-");
    }
}
