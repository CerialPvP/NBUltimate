package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;

public class TickCommand extends AbstractCommand {
    @Override
    public CommandAPICommand getCommandData() {
        return new CommandAPICommand("tick")
                .withArguments(new IntegerArgument("pos"))
                .executes((sender, args) -> {
                    PlayCommand.getSongPlayer().setTick((Integer) args.getOrDefault("pos",0));
                });
    }
}
