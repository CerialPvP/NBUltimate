package cc.cerial.nbultimate.noteblocklib;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.utils.MathUtils;
import cc.cerial.nbultimate.utils.Utils;
import cc.cerial.nbultimate.utils.exceptions.SongPlayerNotSetException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.raphimc.noteblocklib.format.nbs.model.NbsCustomInstrument;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.player.FullNoteConsumer;
import net.raphimc.noteblocklib.player.SongPlayerCallback;
import net.raphimc.noteblocklib.util.Instrument;
import net.raphimc.noteblocklib.util.MinecraftDefinitions;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;

import static cc.cerial.nbultimate.utils.Utils.format;
import static cc.cerial.nbultimate.utils.Utils.getIcon;

/**
 * An implementation of {@link SongPlayerCallback} and {@link FullNoteConsumer} for NBUltimate.
 */
public class NBCallback implements SongPlayerCallback, FullNoteConsumer {
    // NOTE: When this isn't set, make the callback refuse to work!!!
    private NBSongPlayer songPlayer;
    private int nextNoteOctavesDelta;

    /**
     * <h2>REQUIRED METHOD!!!</h2>
     * This method is required to run before starting a song player, so the callback can have a reference to the song player.
     * @param songPlayer The song player you want to attach.
     */
    public void attachSongPlayer(NBSongPlayer songPlayer) {
        this.songPlayer = songPlayer;
    }

    @Override
    public void playNote(Note note) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        // Added octave implementation (ty RK)

        this.nextNoteOctavesDelta = 0;
        if (this.songPlayer.isMoreOctaves())
            this.nextNoteOctavesDelta = MinecraftDefinitions.applyExtendedNotesResourcePack(note);
        else {
            MinecraftDefinitions.instrumentShiftNote(note);
            MinecraftDefinitions.clampNoteKey(note);
        }

        FullNoteConsumer.super.playNote(note);
    }

    @SuppressWarnings({"PatternValidation", "ReassignedVariable"})
    @Override
    public void playNote(Instrument instrument, float pitch, float volume, float panning) {
        if (songPlayer == null) throw new SongPlayerNotSetException();

        // -- Get the sound --
        String name = instrument.mcSoundName();
        if (this.nextNoteOctavesDelta != 0)
            name += "_"+this.nextNoteOctavesDelta;
        Sound sound = Sound.sound(Key.key(name), Sound.Source.MASTER, volume, pitch);

        // -- Get the instrument color --
        Color color = switch (instrument) {
            case HARP ->           Color.fromRGB(237, 134, 66);
            case BASS ->           Color.fromRGB(153, 82, 35);
            case BASS_DRUM ->      Color.fromRGB(166, 166, 166);
            case SNARE ->          Color.fromRGB(250, 245, 145);
            case HAT ->            Color.fromRGB(255, 255, 255);
            case GUITAR ->         Color.fromRGB(232, 21, 225);
            case FLUTE ->          Color.fromRGB(162, 181, 224);
            case BELL ->           Color.fromRGB(255, 170, 0);
            case CHIME ->          Color.fromRGB(165, 230, 250);
            case XYLOPHONE ->      Color.fromRGB(245, 213, 213);
            case IRON_XYLOPHONE -> Color.fromRGB(219, 217, 217);
            case COW_BELL ->       Color.fromRGB(107, 83, 51);
            case DIDGERIDOO ->     Color.fromRGB(217, 130, 43);
            case BIT ->            Color.fromRGB(87, 217, 43);
            case BANJO ->          Color.fromRGB(199, 187, 56);
            case PLING ->          Color.fromRGB(237, 200, 114);
        };

        this.playSound(sound, panning, color);
    }

    @Override
    public void onFinished() {
        // Because this event runs earlier than the stop event, we will
        // delay the execution of the skip by 10 ticks.
        Bukkit.getScheduler().scheduleSyncDelayedTask(NBUltimate.get(), songPlayer::skip, 10L);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public void playCustomNote(NbsCustomInstrument customInstrument, float pitch, float volume, float panning) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        // TODO: When resource pack downloader is finished, add lookup to get proper namespace of sound. For now, assume namespace is default (minecraft).
        Sound sound = Sound.sound(Key.key(customInstrument.getName()), Sound.Source.MASTER, volume, pitch);
        this.playSound(sound, panning, Color.fromRGB(255, 0, 0));
    }

    private void playSound(Sound sound, float panning, Color particleColor) {
        if (songPlayer == null) throw new SongPlayerNotSetException();
        long ns = System.nanoTime(); // Get nanoseconds for debugging.
        panning = (float) (panning * NBUltimate.getPluginConfig().getPanningSpacing());
        boolean shouldPanningDebug = NBUltimate.getPluginConfig().getPanningDebugging();
        for (Player player: this.songPlayer.getPlayers()) {
            Location loc;
            if (!this.songPlayer.isMono() || panning != 0.0f) {
                loc = MathUtils.stereoPan(player.getLocation(), panning);
                player.playSound(sound, loc.getX(), loc.getY(), loc.getZ());
            } else {
                loc = player.getLocation();
                player.playSound(sound);
            }
            if (shouldPanningDebug) loc.getWorld().spawnParticle(Particle.DUST, loc.clone().set(loc.getX(), loc.getY() + 0.5, loc.getZ()), 1, new Particle.DustOptions(particleColor, 1));
        }
        if (NBUltimate.getPluginConfig().getNoteDebugging()) {
            Utils.sendToOps(format("<yellow>[%s]</yellow> <gold><bold>Sound:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Pitch:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Volume:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Panning:</bold></gold> <yellow>%s</yellow> <gray>|</gray> <gold><bold>Time:</bold></gold> <yellow>%sns</yellow>", this.songPlayer.getName(), sound, sound.pitch(), sound.volume(), panning, (System.nanoTime() - ns)));
        }
    }
}
