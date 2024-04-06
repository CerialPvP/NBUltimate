package cc.cerial.nbultimate;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class Utils {
    public static Component getPrefix() {
        return MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient>");
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

    @Nullable
    public static List<String> getAllFiles(String dir) {
        File dirFile = new File(dir);
        if (dirFile.listFiles() == null) {
            return null;
        }

        List<String> files = new ArrayList<>();
        for (File loopfile: Objects.requireNonNull(dirFile.listFiles())) {
            if (loopfile.isDirectory()) {
                List<String> childFiles = getAllFiles(loopfile.getPath());
                if (childFiles == null) continue;
                files.addAll(childFiles);
            } else {
                String path = loopfile.getPath();
                boolean isMatching = Pattern
                        .compile("\\.(nbs|mcsp2|mid|txt|notebot)", Pattern.CASE_INSENSITIVE)
                        .matcher(path)
                        .find();
                if (!isMatching) continue;
                files.add(loopfile.getPath().replace("plugins/NBUltimate/songs/", ""));
            }
        }

        return files;
    }

    public static String replaceIfBlank(String origin, String replace) {
        if (origin.isBlank()) {
            return replace;
        }
        return origin;
    }

    public static Object replaceIfNull(@Nullable Object origin, String replace) {
        if (origin == null) {
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

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }
}
