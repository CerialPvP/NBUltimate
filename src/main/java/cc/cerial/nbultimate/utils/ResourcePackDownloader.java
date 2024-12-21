package cc.cerial.nbultimate.utils;

import cc.cerial.nbultimate.utils.exceptions.MissingValuesException;
import org.bukkit.Bukkit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.*;
import java.util.List;

import static cc.cerial.nbultimate.utils.Utils.format;

public class ResourcePackDownloader {
    public static class Builder {
        private Version mcPack;
        private List<String> customPacks;

        /**
         * Provide a Minecraft server version to download a resource pack for.
         * @param ver The version you want to download a resource pack for.
         * @return This class, for method chaining.
         */
        public Builder minecraftPack(Version ver) {
            this.mcPack = ver;
            return this;
        }

        /**
         * Provide a custom resource pack URL.<br>
         * <h3>WARNING: The URL you provide must be a direct download URL, and must be a ZIP file.</h3>
         * @param assets A list of custom pack URLs that will be added to the list of downloaded packs.
         * @return This class, for method chaining.
         */
        public Builder customAssets(List<String> assets) {
            this.customPacks.addAll(assets);
            return this;
        }

        public ResourcePackDownloader build() {
            if (this.mcPack == null)
                throw new MissingValuesException("You must provide a server version to download using ResourcePackDownloader.Builder#minecraftPack(Version).");

            return new ResourcePackDownloader(this);
        }
    }

    private Version mcPack;
    private List<String> customPacks;

    private ResourcePackDownloader(Builder b) {
        this.mcPack = b.mcPack;
        this.customPacks = b.customPacks;
    }

    // https://github.com/InventivetalentDev/minecraft-assets/zipball/refs/heads/1.21.3

    public void download() {
        // Download Minecraft pack
        try {
            download(new URI("https://github.com/InventivetalentDev/minecraft-assets/zipball/refs/heads/"+mcPack).toURL());
        } catch (URISyntaxException | MalformedURLException e) {
            Bukkit.broadcast(format("<red>%s Invalid vanilla pack URL <u>%s</u>.</red>", Utils.getIcon(Utils.IconTypes.ERROR), mcPack), "nbultimate.download");
            return;
        }

        // Download custom packs if they exist.
        if (this.customPacks.isEmpty()) return;
        for (String pack: this.customPacks) {
            try {
                download(new URI(pack).toURL());
            } catch (MalformedURLException | URISyntaxException e) {
                Bukkit.broadcast(format("<red>%s Couldn't download custom pack with URL %s.</red>", Utils.getIcon(Utils.IconTypes.ERROR), pack), "nbultimate.download");
            }
        }
    }

    private String getEnumFromCode(int code) {
        for (Field field: HttpURLConnection.class.getFields()) {
            try {
                if ((int) field.get(null) == code)
                    return field.getName()
                            .replace("HTTP_", "")
                            .toLowerCase();
            } catch (IllegalAccessException ignored) {}
        }

        return null;
    }

    private void download(URL url) {
        try (FileOutputStream fos = new FileOutputStream("plugins/NBUltimate/resources/"+url.getFile())) {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int res = connection.getResponseCode();
            if (res != HttpURLConnection.HTTP_OK) {
                Bukkit.broadcast(format("<red>%s Couldn't retrieve download content for <u>%s</u>: Received status code %s (%s)<red>", Utils.getIcon(Utils.IconTypes.ERROR), url, res, getEnumFromCode(res)), "nbultimate.download");
                return;
            }

            try (InputStream is = connection.getInputStream()) {
                int contentLength = connection.getContentLength();
                byte[] buffer = new byte[8192];

                int bytesRead;
                long totalBytesRead = 0;
                long lastSent = 0;

                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    if (System.currentTimeMillis()-lastSent < 1000) continue;

                    // Some websites don't give the file size.
                    if (contentLength == 0)
                        Bukkit.broadcast(format("<yellow>%s</yellow> <#8a4007>%s</#8a4007> | <#ed8b40>%s Downloaded</#ed8b40>",
                                Utils.getIcon(Utils.IconTypes.WAIT), url, byteToString(totalBytesRead)), "nbultimate.download");
                    else
                        Bukkit.broadcast(format("<yellow>%s</yellow> <#8a4007>%s</#8a4007> | <#ed8b40>%s/%s Downloaded</#ed8b40>",
                                Utils.getIcon(Utils.IconTypes.WAIT), url, byteToString(totalBytesRead), byteToString(contentLength)), "nbultimate.download");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"ReassignedVariable", "MalformedFormatString"})
    private String byteToString(long bytes) {
        if (bytes == 0) return "0B";

        String[] units = {"B", "KB", "MB", "GB"};
        int i = 0;
        while (bytes >= 1024) {
            bytes /= 1024;
            i++;
        }
        return String.format("%.2f%s", bytes, units[i]);
    }
}
