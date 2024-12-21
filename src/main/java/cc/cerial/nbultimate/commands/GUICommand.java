package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.guis.TestGUI;
import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cc.cerial.nbultimate.utils.Utils.format;

public class GUICommand extends AbstractCommand {
    @Override
    public CommandAPICommand getCommandData() {
        return new CommandAPICommand("gui").executes(this::execute);
    }

    private void execute(CommandSender sender, CommandArguments args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(format("<red>You must be a player to run this command.</red>"));
            return;
        }

        TestGUI.open(p);
    }
}
