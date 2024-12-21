package cc.cerial.nbultimate.utils;

import cc.cerial.nbultimate.NBUltimate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {
    /**
     * Gives a formatted component with the NBUltimate prefix, and allows
     * for formatting using {@link String#format} formatting.
     * @param message The message which you want to send.
     * @param objects The formatted objects.
     * @return A formatted component.
     */
    public static Component format(String message, Object... objects) {
        return MiniMessage.miniMessage().deserialize("<gradient:#8a4007:#ed8b40><bold>NBUltimate</bold></gradient> <dark_gray>»</dark_gray> <reset>"+String.format(message, objects));
    }

    /**
     * Gives a formatted component with the NBUltimate prefix, and allows
     * for formatting using {@link String#format} formatting.<br>
     * <b>WARNING: This function does NOT produce the NBUltimate prefix, and disables italic decoration, as this
     * is meant to be used with item lore. To use italic formatting, simply do {@code <!i>}.</b>
     * @param message The message which you want to send.
     * @param objects The formatted objects.
     * @return A formatted component.
     */
    public static Component formatNoPrefix(String message, Object... objects) {
        return MiniMessage.miniMessage().deserialize(String.format(message, objects)).decoration(TextDecoration.ITALIC, false);
    }

    public static void sendToOps(Component comp) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("op")).toList().forEach(p -> p.sendMessage(comp));
        Bukkit.getConsoleSender().sendMessage(comp);
    }

    /**
     * Icon types to use for the {@link #getIcon} method.
     */
    public enum IconTypes {
        ERROR, SUCCESS, WARNING, WAIT
    }

    /**
     * Get a Unicode icon for a specific thing.
     * @param icon
     * @return
     */
    public static String getIcon(IconTypes icon) {
        return switch (icon) {
            case ERROR -> "❌";
            case SUCCESS -> "✔";
            case WARNING -> "⚠";
            case WAIT -> "\uD83D\uDD51";
        };
    }

    /**
     * Recursively gets the files in a directory.
     * @param dir The directory which you want to check.
     * @return The files in that specific directory.
     */
    public static List<File> getFilesInDir(@NotNull File dir) {
        if (dir.listFiles() == null) return new ArrayList<>();
        List<File> files = new ArrayList<>();
        for (File file: Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile()) files.add(file); else files.addAll(getFilesInDir(file));
        }
        return files;
    }

    public static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static List<String> scrollingText(String text, int cutoff) {
        text = text + " ".repeat(5);
        List<String> scrollList = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            String scrolledText = text.substring(i) + text.substring(0, i);
            if (scrolledText.length() <= cutoff)
                scrollList.add(scrolledText);
            else
                scrollList.add(scrolledText.substring(0, cutoff));
        }
        return scrollList;
    }

    public static String getProperTime(double time) {
        int floor = (int) Math.floor(time/60);
        String mod = (int) (time % 60) + "";
        if (mod.length() < 2) {
            mod = "0" + mod;
        }
        return floor + ":" + mod;
    }

    public static void unzip(File file) {
        NBUltimate n = NBUltimate.get();
        ZipFile f = null;
        try {
            f = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = f.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.getName().startsWith("songs/") && !entry.getName().equalsIgnoreCase("README.txt")) continue;

                File saveTo = new File(n.getDataFolder(), entry.getName());

                if (entry.isDirectory()) {
                    if (!saveTo.mkdirs()) n.getLogger().warning("Couldn't make directory "+saveTo+".");
                    continue;
                }

                try (InputStream is = f.getInputStream(entry); FileOutputStream fos = new FileOutputStream(saveTo)) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                    }
                }
            }
            n.getLogger().info("Successfully generated all default songs.");
        } catch (IOException e) {
            n.getLogger().log(Level.SEVERE, "There was an error while generating files:", e);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException e) {
                    n.getLogger().log(Level.SEVERE, "There was an error when closing the ZipFile:", e);
                }
            }

        }
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public static String getProgressBar(double num1, double num2, int length) {
        int index = (int) Math.floor(num1 / num2 * length);
        String string = "";
        for (int i = 0; i < length; i++) {
            string = string + "▬";
        }
        return "<#ed8b40>" + string.substring(0, index) + "<#8a4007>\uD83D\uDCA9</#8a4007>" +
                string.substring((index + 1 > string.length()) ? index : index + 1) + "</#ed8b40>";
    }

    public static double roundDecimal(double number, int decimals) {
        return Double.parseDouble(new DecimalFormat("#."+"#".repeat(decimals)).format(number));
    }

    public static ItemStack hideTooltip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
}
