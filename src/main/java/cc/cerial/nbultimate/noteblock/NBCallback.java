package cc.cerial.nbultimate.noteblock;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.raphimc.noteblocklib.format.nbs.NbsDefinitions;
import net.raphimc.noteblocklib.format.nbs.NbsSong;
import net.raphimc.noteblocklib.format.nbs.model.NbsNote;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.model.NoteWithPanning;
import net.raphimc.noteblocklib.model.NoteWithVolume;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.ISongPlayerCallback;
import net.raphimc.noteblocklib.util.Instrument;
import net.raphimc.noteblocklib.util.MinecraftDefinitions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class NBCallback implements ISongPlayerCallback {
    private final Song<?,?,?> song;
    private short nps = 0;
    private short prevNps = 0;
    private BukkitTask npsTimer;
    private Set<Player> players;

    public NBCallback(Song<?,?,?> song) {
        this.song = song;
    }

    @SuppressWarnings("deprecation")
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
            if (!(song instanceof NbsSong nbssong)) {
                return null;
            }

            int customID = id-nbssong.getHeader().getVanillaInstrumentCount();
            String[] splitInstrument = nbssong.getData().getCustomInstruments().get(customID).getSoundFileName().split("/");
            return splitInstrument[splitInstrument.length-1].replace(".", "_").replace("_ogg", "").toUpperCase();
        }
    }
    private Location getLocationOfDir(double offset, double div, Location loc) {
        Vector vec = loc.getDirection();
        vec.setY(0).normalize();
        vec.rotateAroundY(Math.PI / div);
        vec.multiply(offset);
        return loc.add(vec);
    }

    private Set<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public BukkitTask getNpsTimer() {
        if (this.npsTimer != null) return this.npsTimer;

        // Create NPS Timer if it doesn't exist.
        this.npsTimer = new BukkitRunnable() {
            private byte tick;

            @Override
            public void run() {
                if (tick == 20) {
                    prevNps = nps;
                    nps = 0;
                    tick = 0;
                }

                tick++;
            }
        }.runTaskTimer(NBUltimate.getInstance(), 0L, 1L);
        return this.npsTimer;
    }

    public short getNps() {
        return prevNps;
    }

    @Override
    public final void playNote(Note note) {
        if (note instanceof NbsNote nbsNote) {
            int pitch = NbsDefinitions.getPitch(nbsNote);
            nbsNote.setKey((byte) NbsDefinitions.getKey(nbsNote));
            nbsNote.setPitch((short) (pitch % NbsDefinitions.PITCHES_PER_KEY));
        }

        // This method makes it so the instrument still sounds good in the 2 octave range.
        MinecraftDefinitions.instrumentShiftNote(note);

        String instrument = getNoteInstrument(note);
        if (instrument == null) return;

        // Calculate pitch
        float pitch;
        if (note instanceof NbsNote nbsNote) {
            pitch = Utils.clamp(MinecraftDefinitions.nbsPitchToMcPitch(NbsDefinitions.getPitch(nbsNote)), 0f, 2f);
        } else {
            pitch = MinecraftDefinitions.mcKeyToMcPitch(MinecraftDefinitions.nbsKeyToMcKey(note.getKey()));
        }

        float volume;
        if (note instanceof NoteWithVolume noteWithVolume) {
            volume = noteWithVolume.getVolume()/100;
        } else {
            volume = 0f;
        }

        float panning;
        if (note instanceof NoteWithPanning noteWithPanning) {
            panning = (float) (noteWithPanning.getPanning()*0.04);
        } else {
            panning = 0f;
        }

        if (NBUltimate.getInstance().getConfig().getBoolean("note-debug"))
            NBUltimate.getAdventure().all().sendMessage(
                    MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> <red>Note Debugging:</red>\n" +
                            "<gray>•</gray> <#ED8B40><bold>Sound:</bold></#ED8B40> <#C9702B>"+instrument+"</#C9702B>\n" +
                            "<gray>•</gray> <#ED8B40><bold>Pitch:</bold></#ED8B40> <#C9702B>"+pitch+"</#C9702B>\n"+
                            "<gray>•</gray> <#ED8B40><bold>Volume:</bold></#ED8B40> <#C9702B>"+volume+"</#C9702B>\n"+
                            "<gray>•</gray> <#ED8B40><bold>Panning:</bold></#ED8B40> <#C9702B>"+panning+"</#C9702B>")
            );

        try {
            for (Player player: getPlayers()) {
                double div;
                if (panning < 0f) {
                    div = -1;
                } else {
                    div = 1;
                }

                Location loc = getLocationOfDir(panning, div, player.getLocation());
                player.playSound(loc, instrument, volume, pitch);
                nps++;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
