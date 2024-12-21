package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import cc.cerial.nbultimate.utils.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

import static cc.cerial.nbultimate.utils.Utils.format;

public class ReloadCommand extends AbstractCommand {
    @Override
    public CommandAPICommand getCommandData() {
        return new CommandAPICommand("reload")
                .withPermission("nbultimate.reload")
                .executes(this::execute);
    }

    private void execute(CommandSender sender, CommandArguments args) {
        NBUltimate.loadConfig();
        NBUltimate.get().registerCommandAPI(true);
        sender.sendMessage(format("<green>%s Reloaded the configuration successfully.</green>", Utils.getIcon(Utils.IconTypes.SUCCESS)));
    }
}
