package cc.cerial.nbultimate.utils.exceptions;

/**
 * This exception is thrown from {@link cc.cerial.nbultimate.noteblocklib.NBSongPlayer.Builder}, when there isn't a song or players set.
 */
public class MissingValuesException extends RuntimeException {
    public MissingValuesException(String message) {
        super(message);
    }
}
