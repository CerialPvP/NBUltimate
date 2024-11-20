package cc.cerial.nbultimate.utils.exceptions;

/**
 * This exception is thrown when a callback doesn't have a song player attached to it.
 */
public class SongPlayerNotSetException extends RuntimeException {
    public SongPlayerNotSetException() {
        super("A callback does not have a song player attached to it. Please attach a song player using NBCallback#attachSongPlayer(NBSongPlayer).");
    }
}
