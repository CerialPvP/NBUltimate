package cc.cerial.nbultimate.noteblock;

import cc.cerial.nbultimate.NBUltimate;
import net.raphimc.noteblocklib.model.Song;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

public class GlobalSongPlayer extends BaseSongPlayer{
    private NBCallback callback;

    public GlobalSongPlayer(Song<?, ?, ?> song, NBCallback callback) {
        super(song, callback);
        callback.setPlayers(Set.copyOf(Bukkit.getOnlinePlayers()));
        this.callback = callback;
    }

    @Override
    public Set<Player> getPlayers() {
        this.callback.setPlayers(Set.copyOf(Bukkit.getOnlinePlayers()));
        return Set.copyOf(Bukkit.getOnlinePlayers());
    }

    @Override
    public void play() {
        if (NBUltimate.songPlayers.containsKey(this)) throw new IllegalStateException("There is already an instance of this song player.");
        super.play();
        NBUltimate.songPlayers.put(this, null);
    }

    @Override
    public void stop() {
        super.stop();
        if (!NBUltimate.songPlayers.containsKey(this)) throw new IllegalStateException("The songplayers map doesn't contain this songplayer.");
        NBUltimate.songPlayers.remove(this);
    }

}
