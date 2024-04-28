package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBSong;
import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import cc.cerial.nbultimate.noteblock.BaseSongPlayer;
import cc.cerial.nbultimate.noteblock.GlobalSongPlayer;
import cc.cerial.nbultimate.noteblock.NBCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.format.midi.MidiSong;
import net.raphimc.noteblocklib.format.nbs.NbsSong;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.model.SongView;
import net.raphimc.noteblocklib.util.SongResampler;
import net.raphimc.noteblocklib.util.SongUtil;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import javax.sound.midi.MidiFileFormat;
import java.io.File;

public class PlayCommand {
    @Description("Allows you to play songs from the songs folder.")
    @CommandPermission("nbultimate.play")
    @Command("nb play")
    @AutoComplete("@songs *")
    public void play(
            BukkitCommandActor actor,
            @NBSong String song,
            @Switch("no-progressbar") boolean noProgress
    ) {
        // Add "plugins/NBUltimate/songs" if there isn't and make a file instance.
        if (!song.contains("plugins/NBUltimate/songs/")) {
            song = "plugins/NBUltimate/songs/"+song;
        }

        File file = new File(song);
        Song<?,?,?> nbsong;
        try {
            nbsong = NoteBlockLib.readSong(file);
        } catch (Exception e) {
            actor.audience().sendMessage(
                    MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> "+
                            "<red>Couldn't load song"+file.getName()+". Is the song format valid? If you have admin access, check console.</red>")
            );
            e.printStackTrace();
            return;
        }

        // Remove any silent notes
        SongUtil.removeSilentNotes(nbsong.getView());

        // Information
        SongView<?> view = nbsong.getView();
        String title = Utils.replaceIfBlank(view.getTitle(), "Unknown Title");
        String name = file.getName();
        String author = "Unavailable"; // Retrieved via format
        String ogauthor = "Unavailable"; // Retrieved only via NBS
        float speed = view.getSpeed();
        float bpmspeed = speed * 15;
        int oglength = view.getLength();
        String length = Utils.calcTime(oglength / speed);

        BaseSongPlayer songPlayer;
        if (nbsong instanceof NbsSong nbsSong) {
            SongResampler.applyNbsTempoChangers(nbsSong);
            author = Utils.replaceIfBlank(nbsSong.getHeader().getAuthor(), "Unknown Author");
            ogauthor = Utils.replaceIfBlank(nbsSong.getHeader().getOriginalAuthor(), "Unknown Original Author");
            songPlayer = new GlobalSongPlayer(nbsSong, new NBCallback(nbsSong));
        } else if (nbsong instanceof MidiSong midiSong) {
            MidiFileFormat mff = midiSong.getHeader().getMidiFileFormat();
            // https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/sound/midi/MidiFileFormat.html
            author = (String) Utils.replaceIfNull(mff.properties().get("author"), "Unknown Author");
            songPlayer = new GlobalSongPlayer(midiSong, new NBCallback(midiSong));
        } else {
            songPlayer = new GlobalSongPlayer(nbsong, new NBCallback(nbsong));
        }
        songPlayer.setShowProgress(!noProgress);

        // New queue system
        try {
            songPlayer.play();
        } catch (IllegalStateException ex) {
            actor.audience().sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            "<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> "+
                                  "<red>There is already a song on the global song player playing.</red>"
                    )
            );
            return;
        }

        NBUltimate.getAdventure().all().sendMessage(
                MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> <gold>Currently playing:</gold>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The title of the song, set in the OpenNBS program.</#ED8B40>'>Song Title:</hover></bold></#ED8B40> <#C9702B>"+title+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The name of the song file.</#ED8B40>'>Track File Name:</hover></bold></#ED8B40> <#C9702B>"+name+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The author of the song, set in the OpenNBS program.</#ED8B40>'>File Author:</hover></bold></#ED8B40> <#C9702B>"+author+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The original author of the song, set in the OpenNBS program.</#ED8B40>'>Original Author:</hover></bold></#ED8B40> <#C9702B>"+ogauthor+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The speed of the song, measured in Ticks Per Second and Beats Per Minute.</#ED8B40>'>Speed:</hover></bold></#ED8B40> <#C9702B>"+speed+" TPS / "+bpmspeed+" BPM</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The length of the song, in minutes and seconds.</#ED8B40>'>Length:</hover></bold></#ED8B40> <#C9702B>"+length+"</#C9702B>")
        );
    }
}
