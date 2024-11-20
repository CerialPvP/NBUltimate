package cc.cerial.nbultimate.utils;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import static cc.cerial.nbultimate.utils.Utils.format;

@SuppressWarnings("UnstableApiUsage")
public class CommandUtils {
    /**
     * Requires a player to be retrieved. If the sender of the provided command context is <i>not</i> a player, the
     * sender will receive a message that they must be a player to run this command.<br>
     * <b>NOTE:</b> If this returns null, return 0 on the command, to mark it as an unsuccessful command.
     * @param ctx The context from the command.
     * @return The player from the context (if the command executor is a player).
     */
    @Nullable
    public static Player requirePlayer(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(format("%s You must be a player to run this command.", Utils.getIcon(Utils.IconTypes.ERROR)));
            return null;
        }

        return (Player) sender;
    }
}
