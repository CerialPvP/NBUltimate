package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import cc.cerial.nbultimate.noteblock.NBCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.format.nbs.NbsSong;
import net.raphimc.noteblocklib.player.SongPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;

public class PlayCommand {
    @Description("Allows you to play songs from the songs folder.")
    @CommandPermission("nbultimate.play")
    @Command("nb play")
    @AutoComplete("@songs *")
    public void play(
            BukkitCommandActor actor,
            String song
    ) {
        // Add "plugins/NBUltimate/songs" if there isn't and make a file instance.
        if (!song.contains("plugins/NBUltimate/songs/")) {
            song = "plugins/NBUltimate/songs/"+song;
        }

        File file = new File(song);
        NbsSong nbsong;
        try {
            nbsong = (NbsSong) NoteBlockLib.readSong(file);
        } catch (Exception e) {
            Utils.sendMessage(actor.getSender(), "&cAn error has occurred while reading the song &n"+song+"&c.");
            return;
        }

        // Information
        String title = Utils.replaceIfBlank(nbsong.getHeader().getTitle(), "Unknown Title");
        String name = file.getName();
        String author = Utils.replaceIfBlank(nbsong.getHeader().getAuthor(), "Unknown Author");
        String ogauthor = Utils.replaceIfBlank(nbsong.getHeader().getOriginalAuthor(), "Unknown Original Author");
        double speed = (double) nbsong.getHeader().getSpeed() / 100;
        double bpmspeed = speed * 15;
        double oglength = nbsong.getHeader().getLength();
        String length = Utils.calcTime(oglength/speed);

        MiniMessage mm = MiniMessage.miniMessage();
        Component message = Utils.getPrefix().append(
                mm.deserialize(" <dark_gray>></dark_gray> <gold>Currently playing:</gold>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The title of the song, set in the OpenNBS program.'>Song Title:</hover></bold></#ED8B40> <#C9702B>"+title+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The name of the song file.'>Track File Name:</hover></bold></#ED8B40> <#C9702B>"+name+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The author of the song, set in the OpenNBS program.'>File Author:</hover></bold></#ED8B40> <#C9702B>"+author+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The original author of the song, set in the OpenNBS program.'>Original Title:</hover></bold></#ED8B40> <#C9702B>"+ogauthor+"</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The speed of the song, measured in Ticks Per Second and Beats Per Minute.'>Speed:</hover></bold></#ED8B40> <#C9702B>"+speed+" TPS / "+bpmspeed+" BPM</#C9702B>\n" +
                        "<gray>•</gray> <#ED8B40><bold><hover:show_text:'<#ED8B40>The length of the song, in minutes and seconds.'>Length:</hover></bold></#ED8B40> <#C9702B>"+length+"</#C9702B>")
        );
        // Send to all players and console
        NBUltimate.getAdventure().console().sendMessage(message);
        for (Player player: Bukkit.getOnlinePlayers()) {
            NBUltimate.getAdventure().player(player).sendMessage(message);
        }

        // TODO: Write actual queue manager
        SongPlayer player = new SongPlayer(nbsong.getView(), new NBCallback(nbsong));
        player.play();
    }
}
