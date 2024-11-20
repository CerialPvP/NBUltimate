package cc.cerial.nbultimate;

import cc.cerial.nbultimate.managers.commands.AbstractCommand;
import cc.cerial.nbultimate.managers.commands.CommandList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provided as a bootstrap to load commands.
 */
@SuppressWarnings("UnstableApiUsage")
public class CommandBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLogger().info("Loading commands...");
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nb")
                    .executes(ctx -> {
                        Bukkit.getServer().dispatchCommand(ctx.getSource().getSender(), "nb help");
                        return Command.SINGLE_SUCCESS;
                    });
            List<AbstractCommand> commandsList = new ArrayList<>();
            // Use ClassGraph to get all classes in commands package
            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .addClassLoader(context.getClass().getClassLoader())
                    .acceptPackages("cc.cerial.nbultimate.commands")
                    .scan()) {
                for (ClassInfo info: scanResult.getAllClasses()) {
                    try {
                        AbstractCommand cmd = (AbstractCommand) info.loadClass().getDeclaredConstructor().newInstance();
                        root.then(cmd.commandData());
                        commandsList.add(cmd);
                        commandRegistered(context.getLogger(), cmd.commandData(), cmd);
                    } catch (InvocationTargetException | InstantiationException |
                             IllegalAccessException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }

                CommandList.setCommands(commandsList);
            }

            commands.register(root.build(), "The main command for NBUltimate.");
        });
    }

    private void commandRegistered(ComponentLogger logger, LiteralCommandNode<CommandSourceStack> command, AbstractCommand clazz) {
        String name = command.getName();
        logger.info("Registered command named {} from class {}", name, clazz.getClass().getSimpleName());
    }
}
