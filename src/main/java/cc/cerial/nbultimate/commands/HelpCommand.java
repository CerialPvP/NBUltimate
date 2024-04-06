package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.help.CommandHelp;

public class HelpCommand {
    @Command("nb help")
    public void help(
            BukkitCommandActor actor,
            CommandHelp<Component> helpEntries,
            @Default("1") int page
    ) {
        actor.audience().sendMessage(
                Utils.getPrefix()
                        .append(Component.text(" > ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Commands list:", NamedTextColor.GOLD))
        );
        for (Component entry: helpEntries.paginate(page, 7)) {
            actor.audience().sendMessage(entry);
        }
    }
}
