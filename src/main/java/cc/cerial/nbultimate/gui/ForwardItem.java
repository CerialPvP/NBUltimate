package cc.cerial.nbultimate.gui;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

public class ForwardItem extends PageItem {
    public ForwardItem() {
        super(true);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                MiniMessage.miniMessage().deserialize("<#ed8b40>➡ Go Forward<#ed8b40>")
        );
        MiniMessage mm = MiniMessage.miniMessage();
        String pageNum = (pagedGui.getCurrentPage() + 1) != pagedGui.getPageAmount()
                ? "<gray>-</gray> <#ed8b40><bold>Target Page:</bold></#ed8b40> <#C9702B>"+(pagedGui.getCurrentPage()+2)+"/"+pagedGui.getPageAmount()+"</#C9702B>"
                : "<red>You are at the last page.</red>";
        meta.lore(
                List.of(mm.deserialize("<gray>Click here to go a page forward.</gray>"), mm.deserialize(pageNum))
        );
        item.setItemMeta(meta);
        return new ItemWrapper(item);
    }
}
