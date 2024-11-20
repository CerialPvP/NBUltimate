package cc.cerial.nbultimate.noteblocklib;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.utils.MathUtils;
import cc.cerial.nbultimate.utils.Utils;
import cc.cerial.nbultimate.utils.exceptions.SongPlayerNotSetException;
import net.raphimc.noteblocklib.format.nbs.model.NbsCustomInstrument;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.FullNoteConsumer;
import net.raphimc.noteblocklib.player.SongPlayerCallback;
import net.raphimc.noteblocklib.util.Instrument;
import net.raphimc.noteblocklib.util.MinecraftDefinitions;
import net.raphimc.noteblocklib.util.SongResampler;
import net.raphimc.noteblocklib.util.SongUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Objects;

import static cc.cerial.nbultimate.utils.Utils.format;

/**
 * An implementation of {@link SongPlayerCallback} and {@link FullNoteConsumer} for NBUltimate.
 */
public class NBCallback implements SongPlayerCallback, FullNoteConsumer {
    private final Song<?,?,?> song;
    private final boolean isMono;
    private final boolean shouldDeduplicate;
    private final boolean shouldRemoveQuietNotes;
    // NOTE: When this isn't set, make the callback refuse to work!!!
    private NBSongPlayer songPlayer;

    public NBCallback(Song<?,?,?> song, boolean isMono, boolean shouldDeduplicate, boolean shouldRemoveQuietNotes) {
        this.song = song;
        this.isMono = isMono;
        this.shouldDeduplicate = shouldDeduplicate;
        this.shouldRemoveQuietNotes = shouldRemoveQuietNotes;
    }

    public void attachSongPlayer(NBSongPlayer songPlayer) {
        this.songPlayer = songPlayer;
    }

    @Override
    public void playNote(Note note) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        // TODO: Add 10 octave support
        MinecraftDefinitions.instrumentShiftNote(note);
        MinecraftDefinitions.clampNoteKey(note);
        if (shouldDeduplicate)
            SongUtil.removeDoubleNotes(song.getView());
        if (shouldRemoveQuietNotes)
            SongUtil.removeSilentNotes(song.getView());
        FullNoteConsumer.super.playNote(note);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void playNote(Instrument instrument, float pitch, float volume, float panning) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        this.playSound(Objects.requireNonNull(org.bukkit.Instrument.getByType(instrument.mcId())).getSound(), pitch, volume, panning);
    }

    @Override
    public void onFinished() {
        // Because this event runs earlier than the stop event, we will
        // delay the execution of the skip by 10 ticks.
        Bukkit.getScheduler().scheduleSyncDelayedTask(NBUltimate.get(), songPlayer::skip, 10L);
    }

    @Override
    public void playCustomNote(NbsCustomInstrument customInstrument, float pitch, float volume, float panning) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        Sound sound;
        try {
            sound = Sound.valueOf(customInstrument.getName().replace(".", "_").toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return;
        }
        this.playSound(sound, pitch, volume, panning);
    }

    private void playSound(Sound sound, float pitch, float volume, float panning) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        long ns = System.nanoTime(); // Get nanoseconds for debugging.
        panning = (float) (panning * NBUltimate.getPluginConfig().getPanningSpacing());
        boolean shouldPanningDebug = NBUltimate.getPluginConfig().getPanningDebugging();
        for (Player player: this.songPlayer.getPlayers()) {
            Location loc;
            if (!this.isMono || panning != 0) {
                loc = MathUtils.stereoPan(player.getEyeLocation(), panning);
                player.playSound(loc, sound, volume, pitch);
            } else {
                loc = player.getEyeLocation();
                player.playSound(player, sound, volume, pitch);
            }
            if (shouldPanningDebug) loc.getWorld().spawnParticle(Particle.DUST, loc.clone().set(loc.getX(), loc.getY() + 0.5, loc.getZ()), 1, new Particle.DustOptions(Color.RED, 1));
        }
        if (NBUltimate.getPluginConfig().getNoteDebugging()) {
            Utils.sendToOps(format("<gold><bold>Sound:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Pitch:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Volume:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Panning:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Time:</bold></gold> <yellow>%sns</yellow>", sound, pitch, volume, panning, (System.nanoTime() - ns)));
        }
    }
}
