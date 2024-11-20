package cc.cerial.nbultimate.noteblocklib;

import cc.cerial.nbultimate.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.raphimc.noteblocklib.model.Song;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import static cc.cerial.nbultimate.utils.Utils.*;

@ApiStatus.Internal
public class ProgressTask implements Runnable {
    private NBSongPlayer songPlayer;
    private byte currentTick = 0;
    private int scrollIndex = 0;
    private List<String> scrollingText;

    private static MiniMessage MM = MiniMessage.miniMessage();

    public void setSongPlayer(NBSongPlayer songPlayer) {
        this.songPlayer = songPlayer;
    }


    @Override
    public void run() {
        // Add/reset scroll index
        if (currentTick == 5) if (scrollIndex > scrollingText.size()) scrollIndex = 0; else scrollIndex++;
        // Add/reset current tick
        if (currentTick == 10) currentTick = 0; else currentTick++;

        Song<?,?,?> song = this.songPlayer.getPlaylist().get(0);
        float speed = song.getView().getSpeed();
        double current = this.songPlayer.getTick() / speed;
        double length = song.getView().getLength() / speed;

        String icon = (this.songPlayer.isPaused()) ? "<yellow>⏸</yellow>" : "<green>▶</green>";
        String bar = "<#ed8b40>"+getProperTime(current)+"</#ed8b40> <#8a4007>[</#8a4007>"+getProgressBar(current, length, 20)+"<#8a4007>]</#8a4007> <#ed8b40>"+getProperTime(length)+"</#ed8b40>";
        String scroll;
        try {
            scroll = scrollingText.get(scrollIndex);
        } catch (IndexOutOfBoundsException ignored) {
            scroll = scrollingText.getFirst();
            scrollIndex = 0;
        }
        scroll = "<#ed8b40>"+scroll+"</#ed8b40>";

        Component finalText = format("%s <#8a4007>|</#8a4007> %s <#8a4007>|</#8a4007> %s",
                icon, scroll, bar);

        for (Player player: this.songPlayer.getPlayers())
            player.sendActionBar(finalText);

    }

    public void updateSong() {
        this.scrollingText = Utils.scrollingText(this.songPlayer.getPlaylist().get(0).getView().getTitle(), 20);
    }
}
