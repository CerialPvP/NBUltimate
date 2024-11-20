package cc.cerial.nbultimate.managers.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

/**
 * Abstract class for commands.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractCommand {
    /**
     * @return The command data for this command. To define the behavior of the command, you may either
     * define it directly like this:
     * <pre>{@code
     * @Override
     * public void commandData() {
     *     return Commands.literal("test")
     *                 .executes(ctx -> {
     *                     // Define the behavior here.
     *                 })
     *                 .build();
     * }
     * }</pre>
     * Or, you can use a method reference.
     * <pre>{@code
     * @Override
     * public void commandData() {
     *     return Commands.literal("test")
     *                .executes(this::execute)
     *                .build();
     * }
     *
     * private int execute(CommandContext<CommandSourceStack> ctx) {
     *         // Define the behavior here instead.
     *         // If the command is successful, return 1 like this, otherwise return 0 (this is for Brigadier):
     *         return 1;
     * }
     * }</pre>
     */
    public abstract LiteralCommandNode<CommandSourceStack> commandData();

    /**
     * @return The description of the command.
     */
    public abstract String description();
}

