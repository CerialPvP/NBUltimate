package cc.cerial.nbultimate.guis.buttons;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

import static cc.cerial.nbultimate.utils.Utils.formatNoPrefix;

public class BackItem extends PageItem {
    public BackItem() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                formatNoPrefix("<#ed8b40>⬅ Go Back<#ed8b40>")
        );
        String pageNum = pagedGui.getCurrentPage() > 0
                ? "<gray>-</gray> <#ed8b40><bold>Target Page:</bold></#ed8b40> <#C9702B>"+(pagedGui.getCurrentPage()+1)+"/"+pagedGui.getPageAmount()+"</#C9702B>"
                : "<red>You are at the first page.</red>";
        meta.lore(
                List.of(formatNoPrefix("<gray>Click here to go a page back.</gray>"), formatNoPrefix(pageNum))
        );
        item.setItemMeta(meta);
        return new ItemWrapper(item);
    }
}
