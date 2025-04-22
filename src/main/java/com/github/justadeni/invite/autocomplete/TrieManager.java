package com.github.justadeni.invite.autocomplete;

import com.github.justadeni.invite.Invite;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrieManager {

    private static boolean isReady = false;

    private static AsciiTree trie;

    public static void downloadAndBuild() {
        File players = new File(Invite.getPlugin().getDataFolder(), "players.txt");
        try {
            if (players.createNewFile() || players.length() < 500_000_000L) {
                FileDownloader.Result result = FileDownloader.downloadFile("https://media.githubusercontent.com/media/justADeni/Invite/refs/heads/master/players.txt", players.getPath());
                if (result instanceof FileDownloader.Result.Failure) {
                    return;
                }
            }
        } catch (IOException e) {
            return;
        }
        List<String> lines = new ArrayList<>(52_000_000); // pre-size to avoid resizing
        try (BufferedReader reader = new BufferedReader(new FileReader(players), 16 * 1024)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
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
