package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.managers.CustomArgs;
import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import cc.cerial.nbultimate.noteblocklib.NBPlaylist;
import cc.cerial.nbultimate.noteblocklib.NBSongPlayer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.raphimc.noteblocklib.model.Song;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Objects;

import static cc.cerial.nbultimate.utils.Utils.format;

public class PlayCommand extends AbstractCommand {
    private static NBSongPlayer songPlayer;

    public static void nukeSongPlayer() {
        Bukkit.broadcast(format("nukeSongPlayer ran"));
        songPlayer = null;
    }

    public static NBSongPlayer getSongPlayer() {
        return songPlayer;
    }

    @Override
    public CommandAPICommand getCommandData() {
        return new CommandAPICommand("play")
                .withArguments(CustomArgs.getSongArg("song"))
                .executes(this::execute);
    }

    private void execute(CommandSender sender, CommandArguments args) {
        Song<?,?,?> song = Objects.requireNonNull((Song<?, ?, ?>) args.get("song"));

        if (songPlayer == null) {
            songPlayer = new NBSongPlayer.Builder()
                    .players(() -> new HashSet<>(Bukkit.getOnlinePlayers()), 20)
                    .playlist(new NBPlaylist(song))
                    .storeSongPlayer(false)
                    .showProgress(true)
                    .shouldBroadcast(true)
                    .moreOctaves(true)
                    .stopEvent(PlayCommand::nukeSongPlayer)
                    .name("Command Song Player")
                    .build();
            songPlayer.play();
        } else {
            songPlayer.getPlaylist().add(song);
            sender.sendMessage("Added song "+song.getView().getTitle()+" to queue.");
        }
    }
}
