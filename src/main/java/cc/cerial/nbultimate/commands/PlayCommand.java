package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import cc.cerial.nbultimate.managers.commands.SongArgument;
import cc.cerial.nbultimate.noteblocklib.NBPlaylist;
import cc.cerial.nbultimate.noteblocklib.NBSongPlayer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.raphimc.noteblocklib.model.Song;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static cc.cerial.nbultimate.utils.Utils.format;

@SuppressWarnings("UnstableApiUsage")
public class PlayCommand extends AbstractCommand {
    private static NBSongPlayer songPlayer;

    private static void nukeSongPlayer() {
        Bukkit.broadcast(format("nukeSongPlayer ran"));
        songPlayer = null;
    }

    public static NBSongPlayer getSongPlayer() {
        return songPlayer;
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> commandData() {
        return Commands.literal("play")
                .executes(this::playNoArgs)
                .then(Commands.argument("song", new SongArgument())
                        .executes(this::playSongArg)
                )
                .build();
    }

    @Override
    public String description() {
        return "Plays a song. Using this command with no arguments will open a GUI.";
    }

    private int playNoArgs(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendMessage("Used command with no arguments.");
        return 0;
    }

    private int playSongArg(CommandContext<CommandSourceStack> ctx) {
        Song<?,?,?> song = ctx.getArgument("song", Song.class);
        if (songPlayer == null) {
            songPlayer = new NBSongPlayer.Builder()
                    .players(Bukkit.getOnlinePlayers().toArray(new Player[0]))
                    .playlist(new NBPlaylist(song))
                    .storeSongPlayer(false)
                    .showProgress(true)
                    .shouldBroadcast(true)
                    .stopEvent(PlayCommand::nukeSongPlayer)
                    .name("")
                    .build();
            songPlayer.play();
        } else {
            songPlayer.getPlaylist().add(song);
            ctx.getSource().getSender().sendMessage("Added song "+song.getView().getTitle()+" to queue.");
        }
        return 1;
    }
}
