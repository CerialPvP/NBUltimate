package cc.cerial.nbultimate.noteblock;

import net.raphimc.noteblocklib.format.nbs.NbsSong;
import net.raphimc.noteblocklib.format.nbs.model.NbsNote;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.model.NoteWithPanning;
import net.raphimc.noteblocklib.model.NoteWithVolume;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.ISongPlayerCallback;
import net.raphimc.noteblocklib.util.Instrument;
import net.raphimc.noteblocklib.util.MinecraftDefinitions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Objects;

public class NBCallback implements ISongPlayerCallback {
    private final Song<?,?,?> song;

    public NBCallback(Song<?,?,?> song) {
        this.song = song;
    }

    @Nullable
    private String getNoteInstrument(Note note) {
        // Get the note instrument ID.
        byte id = note.getInstrument();

        // Attempts to retrieve the instrument.
        Instrument ins = Instrument.fromNbsId(id);
        if (ins != null) {
            return Objects.requireNonNull(org.bukkit.Instrument.getByType(ins.mcId()).getSound()).toString().replaceAll("_", ".").replaceAll("NOTE.BLOCK", "NOTE_BLOCK").toLowerCase();
        } else {
            // Custom instruments should be only available on the NBS Song class.
            if (!(song instanceof NbsSong)) {
                return null;
            }

            NbsSong nbssong = (NbsSong) song;
            int customID = id-nbssong.getHeader().getVanillaInstrumentCount();
            String[] splitInstrument = nbssong.getData().getCustomInstruments().get(customID).getSoundFileName().split("/");
            return splitInstrument[splitInstrument.length-1].replaceAll("\\.", "_").toUpperCase();
        }
    }

    private Location getLocationOfDir(double offset, double div, Location loc) {
        Vector vec = loc.getDirection();
        vec.setY(0).normalize();
        vec.rotateAroundY(Math.PI / div);
        vec.multiply(offset);
        return loc.add(vec);
    }

    @Override
    public void playNote(Note note) {
        // This method makes it so the instrument still sounds good in the 2 octave range.
        MinecraftDefinitions.instrumentShiftNote(note);
        // Sometimes, that method fails, so we need to correct the note.
        if (
                note.getKey() < 33 ||
                note.getKey() > 57
        ) {
            MinecraftDefinitions.transposeNoteKey(note);
        }

        String instrument = getNoteInstrument(note);
        if (instrument == null) return;
        // Getting the pitch.
        float pitch = MinecraftDefinitions.mcKeyToMcPitch(MinecraftDefinitions.nbsKeyToMcKey(note.getKey()));
        float volume;
        if (note instanceof NoteWithVolume noteWithVolume) {
            volume = noteWithVolume.getVolume();
        } else {
            volume = 0f;
        }

        float panning;
        if (note instanceof NoteWithPanning noteWithPanning) {
            panning = (float) (noteWithPanning.getPanning()*0.04);
        } else {
            panning = 0f;
        }

        for (Player player: Bukkit.getOnlinePlayers()) {
            double div;
            if (panning < 0f) {
                div = -1;
            } else {
                div = 1;
            }

            Location loc = getLocationOfDir(panning, div, player.getLocation());
            player.playSound(loc, instrument, volume, pitch);
            // TODO: Debug mode (toggleable using config)
        }
    }
}
