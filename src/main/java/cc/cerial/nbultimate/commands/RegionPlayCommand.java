package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBSong;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.BukkitCommandActor;

public class RegionPlayCommand {
    @Description("[EXPERIMENTAL] Play a song in a specific region.")
    @Command("nb regionplay")
    @AutoComplete("@songs *")
    public void regionPlay(BukkitCommandActor actor, @NBSong String song) {

    }

}
