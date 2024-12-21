package cc.cerial.nbultimate.noteblocklib;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.managers.SongPlayerStorageManager;
import cc.cerial.nbultimate.utils.exceptions.MissingValuesException;
import net.raphimc.noteblocklib.format.midi.MidiSong;
import net.raphimc.noteblocklib.format.nbs.NbsSong;
import net.raphimc.noteblocklib.format.nbs.model.NbsCustomInstrument;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.SongPlayer;
import net.raphimc.noteblocklib.util.SongResampler;
import net.raphimc.noteblocklib.util.SongUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static cc.cerial.nbultimate.utils.Utils.format;

/**
 * An extended version of {@link SongPlayer}.
 * To get an instance of the class, use {@link Builder}
 */
public class NBSongPlayer extends SongPlayer {
    /**
     * A builder class, which builds a {@link NBSongPlayer}.
     */
    public static class Builder {
        private boolean isMoreOctaves = false;
        private boolean showProgress = false;
        private boolean storeSongPlayer = true;
        private boolean isMono = false;
        private boolean isMonoChanged = false;
        private boolean shouldDeduplicate = false;
        private boolean shouldRemoveQuietNotes = false;
        private boolean shouldBroadcast = false;
        private double priority = 0d;
        private String name = "A song player";
        private Runnable stopEvent;
        private Runnable skipEvent;
        private Pair<Supplier<Set<Player>>, Integer> playerGetter;
        private NBPlaylist playlist;

        /**
         * Should this song player not transpose notes, and instead use the extended notes texture pack?<br>
         * NOTE: You MUST use <a href="https://github.com/RaphiMC/NoteBlockLib/raw/refs/heads/main/Extended%20Octave%20Range%20Notes%20Pack.zip">this resource pack</a>, or add the sounds from that pack to your server's resource pack.
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder moreOctaves(boolean value) {
            this.isMoreOctaves = value;
            return this;
        }

        /**
         * Sets a new name for this song player. By default, the name is "A song player".
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder name(String value) {
            this.name = value;
            return this;
        }

        /**
         * Should this song player show a progress bar at the action bar of players?
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder showProgress(boolean value) {
            this.showProgress = value;
            return this;
        }

        /**
         * Should this {@link NBSongPlayer} be stored in the internal song players storage?<br>
         * NOTE: If you set this value to false, the song player will be considered invalid to NBUltimate, and it is expected you handle storing this {@link NBSongPlayer}.
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder storeSongPlayer(boolean value) {
            this.storeSongPlayer = value;
            return this;
        }

        /**
         * What priority should this {@link NBSongPlayer} be? The higher song player priority overrides other song players.
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder priority(double value) {
            this.priority = value;
            return this;
        }

        /**
         * <h3>REQUIRED VALUE</h3>
         * Now, you may ask, why is this a {@link Runnable} and not simply an array of players?<br>
         * My answer to this is, you can simply provide a function in here, so that NBSongPlayer will automatically
         * update the player list. This is good when for example, you want all players in your server to hear the music
         * you're playing, but you also want new people to hear your music, or if you're using something like WorldGuard
         * to play regional music, and you want people only in a specific region to hear your music.
         * @param playerGetter The function that supplies the players.
         * @param period How frequent the provided function should run. The period is in ticks, and can be from 10
         *               (0.5 seconds) to 100 ticks (5 seconds)
         * @return This class, for method chaining.
         */
        public Builder players(@NotNull Supplier<Set<Player>> playerGetter, @Range(from = 10, to = 100) int period) {
            this.playerGetter = Pair.of(playerGetter, period);
            return this;
        }

        /**
         * <h3>REQUIRED VALUE</h3>
         * Set this {@link NBSongPlayer}'s song.
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder playlist(@NotNull NBPlaylist value) {
            this.playlist = value;
            return this;
        }

        /**
         * Sets if this {@link NBSongPlayer} should ignore panning, and instead play songs at the player,
         * instead of offsetting the sound according to the panning.<br>
         * <b>WARNING:</b> By default, the mono value will change depending on the currently played song.
         * This means if you're playing a MIDI or NBS song, mono will be set to false, and if you're playing
         * a TXT, MCSP or Notebot song, mono will be set to true. Setting this value to true or false
         * will force NBUltimate to <i>not</i> change the mono value depending on the song.
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder mono(boolean value) {
            this.isMono = value;
            this.isMonoChanged = true;
            return this;
        }

        /**
         * Sets if this {@link NBSongPlayer} should remove duplicated notes. It saves processing time by not having
         * to process duplicated notes (particularly useful for heavy songs).
         * @param value The new value.
         * @return This class, for method chaining
         */
        public Builder deduplicateNotes(boolean value) {
            this.shouldDeduplicate = value;
            return this;
        }

        /**
         * Sets if this {@link NBSongPlayer} should remove quiet/silent notes. It saves processing time by not playing
         * quiet notes, which players don't hear anyway (particularly useful for heavy songs).
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder removeQuietNotes(boolean value) {
            this.shouldRemoveQuietNotes = value;
            return this;
        }

        /**
         * Sets if this {@link NBSongPlayer} should broadcast information about played music.
         * @param value The new value.
         * @return This class, for method chaining.
         */
        public Builder shouldBroadcast(boolean value) {
            this.shouldBroadcast = value;
            return this;
        }

        /**
         * Passes in a new runnable which will run when a song player has completely come into a halt.
         * @param value The new runnable.
         * @return This class, for method chaining.
         */
        public Builder stopEvent(Runnable value) {
            this.stopEvent = value;
            return this;
        }

        /**
         * Passes in a new runnable which will run when a song player has skipped a song.
         * @param value The new runnable.
         * @return This class, for method chaining.
         */
        public Builder skipEvent(Runnable value) {
            this.skipEvent = value;
            return this;
        }

        /**
         * @return A final {@link NBSongPlayer}.
         * @throws MissingValuesException Thrown if there is no playlist or players set.
         */
        public NBSongPlayer build() throws MissingValuesException {
            // Throw exceptions if playlist or player is not set
            if (this.playlist == null)
                throw new MissingValuesException("The playlist value is missing. Call NBSongPlayer.Builder#playlist(NBPlaylist) to add a song.");
            else if (this.playerGetter == null)
                throw new MissingValuesException("The player getter function is missing. Call NBSongPlayer.Builder#players(Runnable, int) to add a player getter function.");
            // Change mono value if the user didn't override the value.
            Song<?,?,?> newSong = this.playlist.get(0);
            if (!this.isMonoChanged) this.isMono = !(newSong instanceof NbsSong) && !(newSong instanceof MidiSong);
            return new NBSongPlayer(this);
        }
    }

    private final boolean isMoreOctaves;
    private final boolean showProgress;
    private final boolean storeSongPlayer;
    private final double priority;
    private boolean isMono;
    private final boolean isMonoChanged;
    private final boolean shouldDeduplicate;
    private final boolean shouldRemoveQuietNotes;
    private final boolean shouldBroadcast;
    private Pair<Supplier<Set<Player>>, Integer> playerGetter;
    private BukkitTask playerGetterTask;
    private final NBPlaylist playlist;
    private final Runnable stopEvent;
    private final Runnable skipEvent;
    private final String name;
    private NBCallback nbCallback;
    private Pair<Runnable, BukkitTask> progressTask;
    private Set<Player> players;

    /**
     * A private constructor, used by {@link Builder}
     */
    private NBSongPlayer(Builder builder) {
        super(builder.playlist.get(0).getView(), new NBCallback());
        
        this.isMoreOctaves = builder.isMoreOctaves;
        this.showProgress = builder.showProgress;
        this.storeSongPlayer = builder.storeSongPlayer;
        this.priority = builder.priority;
        this.playerGetter = builder.playerGetter;
        this.playlist = builder.playlist;
        this.isMono = builder.isMono;
        this.isMonoChanged = builder.isMonoChanged;
        this.shouldBroadcast = builder.shouldBroadcast;
        this.shouldDeduplicate = builder.shouldDeduplicate;
        this.shouldRemoveQuietNotes = builder.shouldRemoveQuietNotes;
        this.stopEvent = builder.stopEvent;
        this.skipEvent = builder.skipEvent;
        this.name = builder.name;
        this.nbCallback = ((NBCallback) this.callback);
        this.nbCallback.attachSongPlayer(this);
    }

    @Override
    public void setPaused(boolean paused) {
        super.setPaused(paused);
    }

    /**
     * Overrides the original {@link SongPlayer#play()} method, and adds additional functionality.
     * @throws IllegalStateException Thrown if the {@link SongPlayerStorageManager} already has this {@link NBSongPlayer}.
     */
    @Override
    public void play() throws IllegalStateException {
        this.playerGetterTask = Bukkit.getScheduler().runTaskTimer(NBUltimate.get(),
                () -> {
                    Set<Player> players = this.playerGetter.getLeft().get();
                    if (players == null) this.setPaused(true); else this.players = players;
                }, 0L, this.playerGetter.getRight());

        // Modify song view
        Song<?,?,?> song = this.playlist.getFirst();
        if (this.shouldDeduplicate)
            SongUtil.removeDoubleNotes(song.getView());
        if (this.shouldRemoveQuietNotes)
            SongUtil.removeSilentNotes(song.getView());

        if (song instanceof NbsSong nbs) {
            SongResampler.applyNbsTempoChangers(nbs);
        }

        if (this.storeSongPlayer) {
            SongPlayerStorageManager manager = NBUltimate.getSongPlayerStorageManager();
            if (manager.get().contains(this)) throw new IllegalStateException("The SongPlayerStorageManager already has this song player in it!");
            manager.addSongPlayer(this);
        }

        if (this.showProgress) {
            ProgressTask task = new ProgressTask();
            task.setSongPlayer(this);
            task.updateSong();
            this.progressTask = Pair.of(task,
                    Bukkit.getScheduler().runTaskTimer(NBUltimate.get(), task, 0L, 2L));
        }

        // Due to us running a player getter task, the callback complains there are
        // no players, due to the scheduler running tasks on the next tick and not instantly
        // (even when the delay param is set to 0 ticks).
        Bukkit.getScheduler().scheduleSyncDelayedTask(NBUltimate.get(), super::play, 2L);
    }

    /**
     * @return The players in this song player. This list is updated periodically according to the
     *         player getter function provided to this song player.
     */
    public Set<Player> getPlayers() {
        return this.players;
    }

    /**
     * Attempts to skip the song in this {@link NBSongPlayer} to the next song in the playlist. If there is no other song to skip to, then the song player will just stop.
     * @return Returns true if the song was successfully skipped, otherwise false.
     */
    public boolean skip() {
        // Removed this.stop(), due to SongPlayer already calling it.

        // You want to hear a quote from a 16-year-old Java developer?
        // "Reflection exposes everything, just like your mother."
        //                                                   - Cerial

        // So... Time to use reflection! Love to have some code which might get unstable! Yippee!
        // Let's see if there is even another song so we can continue doing this shit!
        Song<?,?,?> newSong;
        try {
            newSong = this.playlist.get(1);
        } catch (IndexOutOfBoundsException ignored) {
            // In old NBUltimate, I was doing the shit in stop in here. No need to do it again, so return false!
            return false;
        }

        this.playlist.remove(this.playlist.get(0));

        // Change the mono status if the builder hasn't manually changed the mono status.
        if (!this.isMonoChanged) this.isMono = !(newSong instanceof NbsSong) && !(newSong instanceof MidiSong);

        // This is where the fun really begins!
        // Now, we set the songView field to a new one!
        try {
            Field viewField = this.getClass().getSuperclass().getDeclaredField("songView");
            setField(this, viewField, newSong.getView());
        } catch (NoSuchFieldException e) {
            return false;
        }


        // Finally, we will set the SongPlayerCallback to a new callback!
        try {
            Field callbackField = this.getClass().getSuperclass().getDeclaredField("callback");
            NBCallback callback = new NBCallback();
            callback.attachSongPlayer(this);
            setField(this, callbackField, callback);
            this.nbCallback = callback;
        } catch (NoSuchFieldException e) {
            return false;
        }


        // Reflection over.
        this.play();
        if (this.skipEvent != null) this.skipEvent.run();
        return true;
    }

    private void setField(Object refObject, Field field, Object newValue) {
        field.setAccessible(true);
        try {
            field.set(refObject, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't set field named "+field.getName()+":", e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        playerGetterTask.cancel();
        if (this.stopEvent != null && !this.getPlaylist().hasNext(1))
            this.stopEvent.run();
        if (this.storeSongPlayer && !this.getPlaylist().hasNext(1))
            NBUltimate.getSongPlayerStorageManager().removeSongPlayer(this);
        if (this.showProgress)
            this.progressTask.getRight().cancel();
    }

    /**
     * @return A boolean, that states if this {@link NBSongPlayer} doesn't transpose notes, and uses the <a href="https://github.com/RaphiMC/NoteBlockLib/raw/refs/heads/main/Extended%20Octave%20Range%20Notes%20Pack.zip">Extended Notes Pack</a>.
     */
    public boolean isMoreOctaves() {
        return isMoreOctaves;
    }

    /**
     * @return A boolean, that states if this {@link NBSongPlayer} should send a progress bar to all players in this song player (unless they are part of a song player with a higher priority)
     */
    public boolean isShowProgress() {
        return showProgress;
    }

    /**
     * @return A boolean, that states if this {@link NBSongPlayer} is stored in the internal song players storage.
     */
    public boolean isStoreSongPlayer() {
        return storeSongPlayer;
    }

    /**
     * @return The priority of this {@link NBSongPlayer}.
     */
    public double getPriority() {
        return priority;
    }

    /**
     * @return The playlist which is used by this {@link NBSongPlayer}.
     */
    public NBPlaylist getPlaylist() {
        return playlist;
    }

    /**
     * @return If this {@link NBSongPlayer} is currently playing music with mono.
     */
    public boolean isMono() {
        return isMono;
    }

    /**
     * @return If this {@link NBSongPlayer} should deduplicate notes of songs (remove duplicate notes).
     */
    public boolean shouldDeduplicate() {
        return shouldDeduplicate;
    }

    /**
     * @return If this {@link NBSongPlayer} should remove quiet/silent notes.
     */
    public boolean shouldRemoveQuietNotes() {
        return shouldRemoveQuietNotes;
    }

    /**
     * @return If this {@link SongPlayer} should broadcast information about played songs.
     */
    public boolean shouldBroadcast() {
        return shouldBroadcast;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "NBSongPlayer(name="+this.name+")";
    }
}
