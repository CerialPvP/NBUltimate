package cc.cerial.nbultimate.guis;

import cc.cerial.nbultimate.NBUltimate;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.window.Window;

public class TestGUI {
    public static void open(Player player) {
        Gui gui = PagedGui.items()
                .setStructure(NBUltimate.getGuiStructure())
                .build();

        Window window = Window.single()
                .setGui(gui)
                .build(player);
        window.open();
    }
}
