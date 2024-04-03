package cc.cerial.nbultimate;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static Component getPrefix() {
        return MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate");
    }

    public static void sendMessage(CommandSender sender, String message) {
        Component comp = getPrefix()
                .append(Component.text(" > ", NamedTextColor.DARK_GRAY))
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(message)); // Serialize the response message
        if (sender instanceof Player player) {
            NBUltimate.getAdventure().player(player).sendMessage(comp);
        } else {
            NBUltimate.getAdventure().console().sendMessage(comp);
        }
    }

    public static void sendToOps(String message) {
        Component comp = getPrefix()
                .append(Component.text(" > ", NamedTextColor.DARK_GRAY))
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(message)); // Serialize the response message

        NBUltimate.getAdventure().console().sendMessage(comp);
        for (Player player: Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("nbultimate.admin")) continue;
            NBUltimate.getAdventure().player(player).sendMessage(comp);
        }
    }

    public static String checkDependency(String plugin, String fver) {
        // Get the PluginManager
        PluginManager pm = Bukkit.getPluginManager();

        // Check if the plugin doesn't exist.
        Plugin plug = pm.getPlugin(plugin);
        if (plug == null) {
            return "Error: "+plugin+" isn't installed. Go to the plugin's download page to download it.";
        }

        // Check if the plugin is disabled.
        if (!plug.isEnabled()) {
            return "Error: "+plugin+" is disabled.";
        }

        // Get versions
        String pver = plug.getDescription().getVersion();
        String[] pvs = pver.split("-");
        String[] pverx1 = pvs[0].split("\\.");

        String[] fvs = fver.split("-");
        String[] fverx1 = fvs[0].split("\\.");

        if (Integer.getInteger(pverx1[0]) < Integer.getInteger(fverx1[0])) {
            return "Error: "+plugin+" is below the required version ("+fver+"). Current version: "+pver+".";
        }

        if (
            Integer.getInteger(pverx1[0]).equals(Integer.getInteger(fverx1[0])) &&
            Integer.getInteger(pverx1[1]) < Integer.getInteger(fverx1[1])
        ) {
            return "Error: "+plugin+" is below the required version ("+fver+"). Current version: "+pver+".";
        }

        if (
            Integer.getInteger(pverx1[0]).equals(Integer.getInteger(fverx1[0])) &&
            Integer.getInteger(pverx1[1]).equals(Integer.getInteger(fverx1[1])) &&
            Integer.getInteger(pverx1[2]) < Integer.getInteger(fverx1[2])
        ) {
            return "Error: "+plugin+" is below the required version ("+fver+"). Current version: "+pver+".";
        }

        return plugin+" is on the required version ("+fver+") or above. Current version: "+pver+".";
    }

    public static List<String> getAllFiles(String dir) {
        File file = new File(dir);
        if (file.listFiles() == null) return Collections.emptyList();

        List<String> list = new ArrayList<>();
        for (File loopFile: file.listFiles()) {
            if (loopFile.isDirectory()) {
                List<String> childList = getAllFiles(loopFile.getPath());
                list.addAll(childList);
            } else {
                if (!loopFile.getPath().endsWith(".nbs")) continue;
                list.add(loopFile.getPath().replaceAll(dir, ""));
            }
        }

        return list;
    }

    public static String replaceIfBlank(String origin, String replace) {
        if (origin.isBlank()) {
            return replace;
        }
        return origin;
    }

    public static String calcTime(double n) {
        long f = Math.round(Math.floor(n/60));
        long mod = Math.round(n % 60);
        String smod = mod + "";
        if (smod.length() < 2) {
            smod = "0"+smod;
        }
        return f + ":" + smod;
    }
}
