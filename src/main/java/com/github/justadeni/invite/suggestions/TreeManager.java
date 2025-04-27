package com.github.justadeni.invite.suggestions;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.config.Config;
import com.github.justadeni.invite.utils.Msg;
import com.github.justadeni.invite.utils.URLFileSize;
import org.bukkit.scheduler.BukkitRunnable;
import org.spoorn.tarlz4java.api.TarLz4Decompressor;
import org.spoorn.tarlz4java.api.TarLz4DecompressorBuilder;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TreeManager {

    private static final ConcurrentHashMap<Char3, TstCache> tree = new ConcurrentHashMap<>();

    public static void downloadAndBuild() {
        final File dataFolder = Invite.getPlugin().getDataFolder();
        dataFolder.mkdir();
        File players = new File(Invite.getPlugin().getDataFolder(), "players");
        if (!players.exists()) {
            File compressedFile = new File(Invite.getPlugin().getDataFolder(), "players.tar.lz4");
            if (!compressedFile.exists()) {
                try {
                    URL url = new URI("https://media.githubusercontent.com/media/justADeni/Invite/refs/heads/master/players.tar.lz4").toURL();
                    final long filesize = URLFileSize.get(url);
                    try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                         FileOutputStream fileOutputStream = new FileOutputStream(compressedFile)) {
                        byte[] dataBuffer = new byte[8192];
                        long totalBytesRead = 0;
                        int bytesRead;
                        int progress = 0;
                        while ((bytesRead = in.read(dataBuffer, 0, 8192)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            int percent = (int) Math.floorDiv(totalBytesRead*100, filesize);
                            if (percent > progress) {
                                progress++;
                                Msg.log("Downloading player names, progress: " + progress + "%");
                            }
                        }
                        Msg.log("Download finished successfully.");
                    } catch (IOException e) {
                        Msg.log("Unknown file error occurred when downloading file. Autocomplete won't be available for Invite plugin.");
                    }
                } catch (URISyntaxException | IOException e) {
                    Msg.log("Error occurred when contacting github server. Autocomplete won't be available for Invite plugin.");
                }
            }
            Msg.log("Decompressing player names file...");
            TarLz4Decompressor decompressor = new TarLz4DecompressorBuilder().build();
            decompressor.decompress(compressedFile.toPath(), dataFolder.toPath());
            compressedFile.delete();
            Msg.log("Player names decompressed, tab completion will now be available.");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                tree.keySet().removeIf(key -> System.currentTimeMillis() - tree.get(key).timestamp > (Config.getInstance().CACHE_SURVIVAL * 1000L));
            }
        }.runTaskTimerAsynchronously(Invite.getPlugin(), 20, Config.getInstance().CACHE_SURVIVAL * 20L);
    }

    private static final String ppfp = Invite.getPlugin().getDataFolder().getPath() + "/players/players/";
    private static final String extension = ".mmfopn";

    public static Set<String> getCompletions(String string) {
        Char3 prefix = new Char3(string);
        TstCache tstCache = tree.get(prefix);
        if (tstCache == null) {
            String pathString = prefix.addPrefixAndSuffix(ppfp, extension);
            Path path = Paths.get(pathString);
            if (Files.exists(path)) {
                try {
                    Tst ternarySearchTree = new Tst();
                    ternarySearchTree.addAll(Files.readAllLines(path));
                    tree.put(prefix, new TstCache(ternarySearchTree));
                    return ternarySearchTree.keysWithPrefix(string, Config.getInstance().FIRST_N_SUGGESTIONS);
                } catch (IOException e) {
                    return Set.of();
                }
            } else {
                return Set.of();
            }
        }
        tstCache.timestamp = System.currentTimeMillis();
        Tst ternarySearchTree = tstCache.tst;
        return ternarySearchTree.keysWithPrefix(string, Config.getInstance().FIRST_N_SUGGESTIONS);
    }

}