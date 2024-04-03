package cc.cerial.nbultimate.commands;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.help.CommandHelp;

public class HelpCommand {
    @Command("nb help")
    public void help(
            CommandActor actor,
            CommandHelp<String> helpEntries,
            @Default("1") int page
    ) {
        for (String entry: helpEntries.paginate(page, 7)) {
            actor.reply(entry);
        }
    }
}
