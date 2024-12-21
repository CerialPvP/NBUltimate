package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class StopCommand extends AbstractCommand {
    @Override
    public CommandAPICommand getCommandData() {
        return new CommandAPICommand("stop")
                .executes(this::execute);
    }

    private void execute(CommandSender sender, CommandArguments args) {
        PlayCommand.getSongPlayer().stop();
//        PlayCommand.nukeSongPlayer();
        sender.sendMessage("nuked sonog player");

    }
}
