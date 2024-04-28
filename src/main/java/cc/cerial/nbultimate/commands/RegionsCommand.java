package cc.cerial.nbultimate.commands;

import cc.cerial.nbultimate.NBUltimate;
import cc.cerial.nbultimate.gui.BackItem;
import cc.cerial.nbultimate.gui.ForwardItem;
import cc.cerial.nbultimate.gui.regions.RegionAddIcon;
import cc.cerial.nbultimate.gui.regions.RegionIcon;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegionsCommand {
    @Description("Manage song player regions with this command (Requires WorldGuard).")
    @CommandPermission("nbultimate.playlist")
    @Command("nb regions")
    public void regions(BukkitCommandActor actor) {
        MiniMessage mm = MiniMessage.miniMessage();
        if (!actor.isPlayer()) {
            actor.audience().sendMessage(
                    mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                                    "<red>You must be a player to run this command!</red>")
            );
            return;
        }

        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !wg.isEnabled()) {
            actor.audience().sendMessage(
                    mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                            "<red>WorldGuard must be installed in order to run this command.</red>")
            );
            return;
        }
        // Get all song regions
        List<Item> regions = new ArrayList<>();
        for (File loopFile: new File(NBUltimate.getInstance().getDataFolder(), "songs/region_songs").listFiles()) {
            if (!loopFile.isDirectory()) continue;
            regions.add(new RegionIcon(loopFile.getName()));
        }

        if (regions.isEmpty()) {
            // Add item which says there are no regions
            ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(
                    mm.deserialize("<reset><red>No song regions available!</red>")
            );
            meta.lore(List.of(
                    mm.deserialize("<reset><gray>Create a new region using the <underline>Add New Region</underline> button.</gray>")
            ));
            item.setItemMeta(meta);
            regions.add(new SimpleItem(new ItemWrapper(item)));
        }



        Gui gui = PagedGui.items()
                .setStructure(
                        "A B C D A B C D A",
                        "B . . . . . . . B",
                        "C . . . . . . . C",
                        "C . . . . . . . C",
                        "B . . . . . . . B",
                        "A B C < + > C D A"
                )
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('A', new SimpleItem(new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE)))
                .addIngredient('B', new SimpleItem(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)))
                .addIngredient('C', new SimpleItem(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)))
                .addIngredient('D', new SimpleItem(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)))
                .addIngredient('<', new BackItem())
                .addIngredient('>', new ForwardItem())
                .addIngredient('+', new RegionAddIcon())
                .setContent(regions)
                .build();

        Window.single()
                .setGui(gui)
                .setTitle("")
                .setViewer(actor.requirePlayer())
                .build()
                .open();
    }
}
