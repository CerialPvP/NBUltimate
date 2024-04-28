package cc.cerial.nbultimate.gui.regions;

import cc.cerial.nbultimate.NBUltimate;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionAddIcon extends AbstractItem implements Listener {
    private Map<Player, BukkitTask> players = new HashMap<>();

    @Override
    public ItemProvider getItemProvider() {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        MiniMessage mm = MiniMessage.miniMessage();
        meta.displayName(
                mm.deserialize("<#ed8b40>Add Region")
        );
        meta.lore(List.of(
                mm.deserialize("<gray>Click here to add a new region.</gray>")
        ));
        item.setItemMeta(meta);

        return new ItemWrapper(item);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        MiniMessage mm = MiniMessage.miniMessage();
        player.closeInventory();
        if (this.players.containsKey(player)) {
            player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                    "<red>You have already requested to add a new region./</red>"));
            return;
        }

        // Create a new folder
        File regionsFolder = new File(NBUltimate.getInstance().getDataFolder(), "songs/region_songs");
        if (!regionsFolder.exists() && !regionsFolder.mkdirs()) {
            player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                    "<red>Couldn't make the region_songs folder. Try to make that folder yourself, and try again.</red>"));
            return;
        }

        // Ask for input, and make the input expire in 30 seconds.
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!players.containsKey(player)) return;

                players.remove(player);
                player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                        "<red>Your time has expired to enter a region name.</red>"));
            }
        }.runTaskLater(NBUltimate.getInstance(), 600L);
        this.players.put(player, task);
        player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                "<yellow>Please enter a WorldGuard region name. You have 30 seconds.</yellow>"));
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        player.sendMessage("Chat event ran");
        if (!this.players.containsKey(player)) return;

        player.sendMessage("Players map contains player");
        e.setCancelled(true);
        player.sendMessage("Cancelled player");
        MiniMessage mm = MiniMessage.miniMessage();

        // Check if the region is a valid WorldGuard region.
        WorldGuard worldGuard = WorldGuard.getInstance();
        player.sendMessage("WG Instance: "+worldGuard);
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(e.message());
        boolean exists = false;
        for (World world: Bukkit.getWorlds()) {
            RegionManager rm = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
            if (rm == null) continue;

            if (rm.getRegions().containsKey(plainMessage)) {
                exists = true;
                break;
            }
        }
        player.sendMessage("Exists: "+exists);

        if (!exists) {
            player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                    "<red>The region <underline>"+plainMessage+"</underline> doesn't exist. Try again.</red>"));
            return;
        }

        // If the region exists, make a new region folder with that name, and then cancel the timer.
        this.players.get(player).cancel();
        this.players.remove(player);
        player.sendMessage("Removed from map and cancelled event.");

        File file = new File(NBUltimate.getInstance().getDataFolder(), "songs/region_songs/"+plainMessage);
        player.sendMessage("File: "+file);
        if (file.exists()) {
            player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                    "<red>The region <underline>"+plainMessage+"</underline> already exists.</red>"));
            return;
        }

        if (!file.exists() && file.mkdirs()) {
            player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                    "<red>Couldn't make a folder for region <underline>"+plainMessage+"</underline>.</red>"));
            return;
        }

        player.sendMessage(mm.deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray></dark_gray> "+
                "<green>The region <underline>"+plainMessage+"</underline> has been made. To add songs to the region, " +
                "go to the <underline>"+NBUltimate.getInstance().getDataFolder()+"songs/region_songs/"+plainMessage+"</underline> folder, and add songs there.</green>"));
    }
}
