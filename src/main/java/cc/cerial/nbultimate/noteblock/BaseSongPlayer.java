package cc.cerial.nbultimate.noteblock;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.SongPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

public class BaseSongPlayer extends SongPlayer {
    private NBCallback callback;
    private boolean showProgress = true;
    private BukkitTask progBarTask;

    public BaseSongPlayer(Song<?,?,?> song, NBCallback callback) {
        super(song.getView(), callback);
        this.callback = callback;
    }

    /**
     * Retrieve the NBCallback from the super class.
     * @return NBCallback from the super class.
     */
    public NBCallback getCallback() {
        return this.callback;
    }

    /**
     * Set the progress bar of the player.
     * @param showProgress The progress bar status of the song player.
     */
    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    /**
     * Get the progress bar status.
     * If this is not set via the setShowProgress method, then this will return true.
     * @return The progress bar status of the song player.
     */
    public boolean getShowProgress() {
        return this.showProgress;
    }

    public Set<Player> getPlayers() {
        return null;
    }

    @Override
    public void play() {
        super.play();

        // Get callback and handle exceptions
        callback.getNpsTimer();

        if (this.showProgress) {
            this.progBarTask = new BukkitRunnable() {
                private long timePlayed = -1L;

                @Override
                public void run() {
                    this.timePlayed++;
                    String time = Utils.calcTime(timePlayed);
                    String totalTime = Utils.calcTime((getSongView().getLength()/ getSongView().getSpeed()));
                    String status;
                    if (!isPaused()) status = "<green>⏵</green>"; else status = "<yellow>⏸</yellow>";
                    for (Player player: getPlayers()) {
                        NBUltimate
                                .getAdventure()
                                .player(player)
                                .sendActionBar(
                                        MiniMessage.miniMessage().deserialize(
                                                "<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> " +
                                                      status + " <#ed8b40>"+time+"</#ed8b40><#8a4007>/</#8a4007><#ed8b40>"+totalTime+"</#ed8b40> " +
                                                      "<#8a4007>|</#8a4007> <#ed8b40>"+getSongView().getTitle()+"</#ed8b40> "+
                                                      "<#8a4007>|</#8a4007> <#ed8b40>"+callback.getNps()+" NPS</#ed8b40>"
                                        )
                                );
                    }
                }
            }.runTaskTimer(NBUltimate.getInstance(), 0L, 20L);
        }
    }

    @Override
    public void stop() {
        super.stop();

        // Stop the NPS timer and the progress bar timer
        callback.getNpsTimer().cancel();
        progBarTask.cancel();
    }
}
