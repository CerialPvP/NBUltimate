package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class ReloadCommand {
    @CommandPermission("nbultimate.admin")
    @Description("Reloads the configuration.")
    @Command("nb reload")
    public void reload(BukkitCommandActor actor) {
        NBUltimate.getInstance().reloadConfig();
        actor.audience().sendMessage(
                Utils.getPrefix()
                        .append(Component.text(" > ", NamedTextColor.GRAY))
                        .append(Component.text("The config has been reloaded successfully.", NamedTextColor.GREEN))
        );
    }
}
