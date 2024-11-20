package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import cc.cerial.nbultimate.managers.commands.FlagSwitchArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class TestCommand extends AbstractCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> commandData() {
        Map<String, Class<?>> fsMap = new HashMap<>();
        fsMap.put("testFlag", Boolean.class);
        fsMap.put("num", Integer.class);
        fsMap.put("nigger", String.class);
        return Commands.literal("test")
                .executes(this::executeDefault)
                .then(
                        Commands.argument("switches", new FlagSwitchArgument(fsMap))
                                .executes(this::executeSwitchesArg)
                )
                .build();
    }

    @Override
    public String description() {
        return "A testing command.";
    }

    private int executeDefault(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendMessage("This is the default behavior.");
        return 1;
    }

    @SuppressWarnings("unchecked")
    private int executeSwitchesArg(CommandContext<CommandSourceStack> ctx) {
        Map<String, Object> flags = ctx.getArgument("switches", Map.class);
        ctx.getSource().getSender().sendMessage(flags.toString());
        return 1;
    }
}
