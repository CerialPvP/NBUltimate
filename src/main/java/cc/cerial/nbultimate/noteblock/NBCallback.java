package cc.cerial.nbultimate.noteblock;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.raphimc.noteblocklib.format.nbs.model.NbsCustomInstrument;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.FullNoteConsumer;
import net.raphimc.noteblocklib.player.SongPlayerCallback;
import net.raphimc.noteblocklib.util.Instrument;
import net.raphimc.noteblocklib.util.MinecraftDefinitions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Set;

public class NBCallback implements SongPlayerCallback, FullNoteConsumer {
    private final Song<?,?,?> song;
    private short nps = 0;
    private short prevNps = 0;
    private BukkitTask npsTimer;
    private Set<Player> players;

    public NBCallback(Song<?,?,?> song) {
        this.song = song;
    }

    private Location getLocationOfDir(Location location, boolean right, float distance) {
        Vector direction = location.getDirection();

        // Modify direction vector based on right side
        direction = right ? direction.clone().add(new Vector(0, 0, distance)) : direction.clone().add(new Vector(0, 0, -distance));

        // Add the scaled vector to player location
        return location.add(direction);

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
    public void playNote(Note note) {
        // This method makes it so the instrument still sounds good in the 2 octave range.
        MinecraftDefinitions.instrumentShiftNote(note);

        FullNoteConsumer.super.playNote(note);
    }

    @Override
    public void playNote(Instrument instrument, float pitch, float volume, float panning) {
        String sound = Objects.requireNonNull(org.bukkit.Instrument.getByType(instrument.mcId()).getSound()).toString().replaceAll("_", ".").replaceAll("NOTE.BLOCK", "NOTE_BLOCK").toLowerCase();
        this.playSound(sound, pitch, volume, panning);
    }

    @Override
    public void playCustomNote(NbsCustomInstrument customInstrument, float pitch, float volume, float panning) {
        String[] splitInstrument = customInstrument.getSoundFileName().split("/");
        String sound = splitInstrument[splitInstrument.length-1].replace(".ogg", "");
        this.playSound(sound, pitch, volume, panning);
    }

    private void playSound(String sound, float pitch, float volume, float panning) {
        pitch = Utils.clamp(pitch, 0f, 2f);
        panning = (float) (panning * 5.5);

        if (NBUltimate.getMainConfig().getNoteDebug())
            NBUltimate.getAdventure().all().sendMessage(
                    MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> <red>Note Debugging:</red>\n" +
                            "<gray>•</gray> <#ED8B40><bold>Sound:</bold></#ED8B40> <#C9702B>"+sound+"</#C9702B>\n" +
                            "<gray>•</gray> <#ED8B40><bold>Pitch:</bold></#ED8B40> <#C9702B>"+pitch+"</#C9702B>\n"+
                            "<gray>•</gray> <#ED8B40><bold>Volume:</bold></#ED8B40> <#C9702B>"+volume+"</#C9702B>\n"+
                            "<gray>•</gray> <#ED8B40><bold>Panning:</bold></#ED8B40> <#C9702B>"+panning+"</#C9702B>")
            );

        try {
            for (Player player: getPlayers()) {
                Location loc = getLocationOfDir(player.getLocation(), (panning > 0), panning);
                player.playSound(loc, sound, volume, pitch);
                nps++;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
