package cc.cerial.nbultimate.noteblocklib;

import net.raphimc.noteblocklib.model.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NBPlaylist {
    private ArrayList<Song<?,?,?>> songs = new ArrayList<>();

    /**
     * The main constructor for an {@link NBPlaylist}.
     * @param songs The songs you want to add to the playlist initially.
     * @throws IllegalArgumentException Thrown if an empty playlist is provided.
     * @throws NullPointerException Thrown if the playlist contains a null element.
     */
    public NBPlaylist(Song<?,?,?>... songs) throws IllegalArgumentException, NullPointerException {
        if (songs.length == 0)
            throw new IllegalArgumentException("An empty playlist was provided.");
        checkNull(songs);
        this.songs.addAll(Arrays.asList(songs));
    }


    private void checkNull(Song<?,?,?>... songs) throws NullPointerException {
        List<Song<?,?,?>> songList = Arrays.asList(songs);
        if (songList.contains(null)) throw new NullPointerException("The playlist contains a null element.");
    }

    /**
     * Inserts an array of songs to the playlist.
     * @param index The starting index of the insertion.
     * @param songs The songs you want to insert.
     * @throws IllegalArgumentException Thrown if an empty array is provided or the index is invalid.
     * @throws NullPointerException Thrown if the array contains a null element.
     */
    public void insert(int index, Song<?,?,?>... songs) throws IllegalArgumentException, NullPointerException {
        if (songs.length == 0)
            throw new IllegalArgumentException("Provided an empty array of songs.");
        if (index > this.songs.size())
            throw new IllegalArgumentException("The index provided is invalid.");
        checkNull(songs);
        this.songs.addAll(index, Arrays.asList(songs));
    }

    /**
     * Adds the specified songs array to the playlist.
     * @param songs The songs which you want to add.
     * @throws IllegalArgumentException Thrown if an empty array is provided.
     * @throws NullPointerException Thrown if the array contains a null element.
     */
    public void add(Song<?,?,?>... songs) throws IllegalArgumentException, NullPointerException {
        if (songs.length == 0)
            throw new IllegalArgumentException("Provided an empty array of songs.");
        checkNull(songs);
        this.songs.addAll(Arrays.asList(songs));
    }

    /**
     * Remove the specified songs from the playlist.
     * @param songs The songs which you want to remove.
     * @throws IllegalArgumentException Thrown if you attempt to remove all songs from the playlist.
     */
    public void remove(Song<?,?,?> songs) throws IllegalArgumentException {
        ArrayList<Song<?, ?, ?>> songsTemp = new ArrayList<>(this.songs);
        songsTemp.removeAll(Collections.singletonList(songs));
        if (!songsTemp.isEmpty()) {
            this.songs = songsTemp;
        } else {
            throw new IllegalArgumentException("Attempted to remove all songs from playlist.");
        }
    }

    /**
     * Get a specific song at the specified index.
     * @param index The index of the song you need.
     * @return The specific song you requested from the specified index.
     */
    public Song<?,?,?> get(int index) {
        return this.songs.get(index);
    }

    /**
     * @return The amount of songs in this playlist.
     */
    public int getAmount() {
        return this.songs.size();
    }

    /**
     * Check if the song at the specified index is the last in this playlist.
     * @param index The index you want to check.
     * @return true if there is a song after specified index.
     */
    public boolean hasNext(int index) {
        return songs.size() > (index + 1);
    }

    /**
     * Check if the song at the specified index exists.
     * @param index The index you want to check.
     * @return true if the song at specified index exists.
     */
    public boolean exists(int index) {
        return songs.size() > index;
    }

    /**
     * Get the index of the specified song.
     * @param song The song you want to check.
     * @return Index of the song. Returns -1 if the song isn't in the playlist.
     */
    public int getIndex(Song<?,?,?> song) {
        return songs.indexOf(song);
    }

    /**
     * Check if the provided song exists in this playlist.
     * @param song The song you want to check.
     * @return true, if the song exists in this playlist.
     */
    public boolean contains(Song<?,?,?> song) {
        return songs.contains(song);
    }

    /**
     * @return A list of all songs in this playlist.
     */
    @SuppressWarnings("unchecked")
    public List<Song<?,?,?>> getSongs() {
        return (List<Song<?, ?, ?>>) songs.clone();
    }

    @Override
    public String toString() {
        return "Playlist"+songs.toString();
    }
}
