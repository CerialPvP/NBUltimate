package cc.cerial.nbultimate.noteblock;

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

}
