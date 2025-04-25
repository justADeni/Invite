package com.github.justadeni.invite.invited;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.config.Config;
import com.github.justadeni.invite.db.BiMap;
import com.github.justadeni.invite.utils.Msg;
import com.github.justadeni.invite.utils.Tread;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TreeManager {

    private static Tst tst;

    private static long lastLoaded = System.currentTimeMillis();

    public static void downloadAndBuild() {
        Invite.getPlugin().getDataFolder().mkdir();
        final File db = new File(Invite.getPlugin().getDataFolder(), "playerdb");
        if (!db.exists()) {
            File players = new File(Invite.getPlugin().getDataFolder(), "players.txt");
            try {
                if (players.createNewFile() || players.length() < 500_000_000L) {
                    try (BufferedInputStream in = new BufferedInputStream(new URI("https://media.githubusercontent.com/media/justADeni/Invite/refs/heads/master/players.txt").toURL().openStream());
                         FileOutputStream fileOutputStream = new FileOutputStream(players.getPath())) {
                        byte[] dataBuffer = new byte[8192];
                        long totalBytesRead = 0;
                        int lastpc = 0;
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 8192)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            if (totalBytesRead / 5385584 > lastpc) {
                                lastpc++;
                                Msg.log("Downloading player names, progress: " + lastpc + "%");
                            }
                        }
                        Msg.log("Download finished successfully.");
                    } catch (IOException | URISyntaxException e) {
                        Msg.log("Unknown file error occurred when downloading file. Autocomplete won't be available for Invite plugin.");
                        return;
                    }
                }
            } catch (IOException e) {
                Msg.log("Unknown file error occurred when creating file. Autocomplete won't be available for Invite plugin.");
                return;
            }
            List<String> lines = new ArrayList<>(52_000_000); // pre-size to avoid resizing
            try (BufferedReader reader = new BufferedReader(new FileReader(players), 16 * 1024)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                Msg.log("Unknown file error occurred when reading file. Autocomplete won't be available for Invite plugin.");
                return;
            }
            Msg.log("Loading names into memory...");
            tst = new Tst();
            tst.addAll(lines);
            Msg.log("Names loaded, autocomplete enabled.");
            save();
            clear();
            players.delete();
        }
    }

    private static void save() {
        if (tst == null)
            return;

        final File db = new File(Invite.getPlugin().getDataFolder(), "playerdb");
        try {
            db.createNewFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
                oos.writeObject(tst);
            }
        } catch (IOException e) {
            Msg.log("Something went very wrong when serializing player names.");
            e.printStackTrace();
        }
    }

    private static void clear() {
        tst = null;
    }

    private static void load() {
        final File db = new File(Invite.getPlugin().getDataFolder(), "playerdb");
        try {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
                tst = (Tst) ois.readObject();
            }
        } catch (IOException | RuntimeException | ClassNotFoundException e) {
            Msg.log("Something went very wrong when deserializing player names.");
            e.printStackTrace();
        }
    }

    public static Set<String> getCompletions(String prefix) {
        if (!Config.getInstance().OFFLINE_SUGGESTIONS)
            return Set.of();

        lastLoaded = System.currentTimeMillis();
        if (tst == null) {
            load();
            Thread.ofVirtual().start(() -> {
                Tread.eep(Config.getInstance().CACHE_SURVIVAL + 100);
                if (System.currentTimeMillis() - lastLoaded >= Config.getInstance().CACHE_SURVIVAL)
                    clear();
            });
        }

        return tst.keysWithPrefix(prefix, Config.getInstance().FIRST_N_SUGGESTIONS);
    }

}
