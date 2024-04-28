package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import cc.cerial.nbultimate.noteblock.BaseSongPlayer;
import cc.cerial.nbultimate.noteblock.GlobalSongPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SongControlCommand {
    @Description("Control the song's progress (in ticks).")
    @CommandPermission("nbultimate.playlist")
    @Command("nb songcontrol")
    public void songControl(
            BukkitCommandActor actor,
            int tick
    ) {
        GlobalSongPlayer gsp = null;
        for (BaseSongPlayer player: NBUltimate.songPlayers.keySet()) {
            if (player.getClass().equals(GlobalSongPlayer.class)) {
                gsp = (GlobalSongPlayer) player;
                break;
            }
        }

        MiniMessage mm = MiniMessage.miniMessage();
        if (gsp == null) {
            actor.reply(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> "+
                    "<red>There is currently no song playing on the global song player.</red>"));
            return;
        }
        gsp.setTick(tick);
        actor.reply(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>></dark_gray> "+
                "<green>Set song's position to "+tick+" ticks ("+(Utils.calcTime(tick/gsp.getSongView().getSpeed()))+")</green>"));
    }
}
