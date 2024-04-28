package cc.cerial.nbultimate.gui.regions;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class RegionIcon extends AbstractItem {
    private String region;
    public RegionIcon(String region) {
        super();
        this.region = region;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = item.getItemMeta();
        MiniMessage mm = MiniMessage.miniMessage();
        meta.displayName(
                mm.deserialize("<#ed8b40>"+this.region)
        );
        meta.lore(List.of(
                mm.deserialize("<gray>Click here to enter this region.</gray>")
        ));
        item.setItemMeta(meta);

        return new ItemWrapper(item);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.setCancelled(true);
    }
}
