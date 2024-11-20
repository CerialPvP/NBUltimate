package cc.cerial.nbultimate.managers;

import cc.cerial.nbultimate.noteblocklib.NBSongPlayer;

import java.util.ArrayList;
import java.util.List;

public class SongPlayerStorageManager {
    private final List<NBSongPlayer> songPlayers = new ArrayList<>();

    public List<NBSongPlayer> get() {
        return this.songPlayers;
    }

    public void addSongPlayer(NBSongPlayer songPlayer) {
        this.songPlayers.add(songPlayer);
    }

    public void removeSongPlayer(NBSongPlayer songPlayer) {
        this.songPlayers.remove(songPlayer);
    }
}
