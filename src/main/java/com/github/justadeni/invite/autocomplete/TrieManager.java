package com.github.justadeni.invite.autocomplete;

import com.github.justadeni.invite.Invite;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TrieManager {

    private static boolean isReady = false;

    private static AsciiTree trie;

    public static void downloadAndBuild() {
        try {
            Invite.getPlugin().getDataFolder().createNewFile();
        } catch (IOException ignored) {}
        File players = new File(Invite.getPlugin().getDataFolder(), "players.txt");
        try {
            if (players.createNewFile() || players.length() < 500_000_000L) {
                try (BufferedInputStream in = new BufferedInputStream(new URI("https://media.githubusercontent.com/media/justADeni/Invite/refs/heads/master/players.txt").toURL().openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(players.getPath())) {
                    byte[] dataBuffer = new byte[8192];
                    int bytesRead;
                    int percent = 0;
                    int i = 0;
                    while ((bytesRead = in.read(dataBuffer, 0, 8192)) != -1) {
                        if (i >= 65750){
                            i = 0;
                            percent++;
                            Invite.log("Downloading player names, progress: " + percent + "%");
                        }
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        i++;
                    }
                    Invite.log("Download finished, autocomplete will now be available for Invite plugin.");
                } catch (IOException | URISyntaxException e) {
                    Invite.log("Unknown file error occurred when downloading file. Autocomplete won't be available for Invite plugin.");
                    return;
                }
            }
        } catch (IOException e) {
            Invite.log("Unknown file error occurred when creating file. Autocomplete won't be available for Invite plugin.");
            return;
        }
        List<String> lines = new ArrayList<>(52_000_000); // pre-size to avoid resizing
        try (BufferedReader reader = new BufferedReader(new FileReader(players), 16 * 1024)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Invite.log("Unknown file error occurred when reading file. Autocomplete won't be available for Invite plugin.");
            return;
        }
        trie = new AsciiTree(lines);
        isReady = true;
    }

    public static List<String> getCompletions(String prefix) {
        if (!isReady)
            return List.of();

        return trie.autocomplete(prefix);
    }

}
