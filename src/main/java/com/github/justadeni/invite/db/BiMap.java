package com.github.justadeni.invite.db;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.utils.Msg;

import java.io.*;
import java.util.*;

public class BiMap<K, V> implements Serializable {
    private BiMap() {}

    private final Map<K, Set<V>> forward = new HashMap<>();

    public void put(K key, V value) {
        forward.computeIfAbsent(key, k -> new HashSet<>()).add(value);
    }

    public Set<V> getForward(K key) {
        return forward.getOrDefault(key, Collections.emptySet());
    }

    public Set<K> getKeys() {
        return forward.keySet();
    }

    public int countValues(K key) {
        return forward.getOrDefault(key, Collections.emptySet()).size();
    }

    public void save() {
        Thread.ofVirtual().start(() -> {
            final File db = new File(Invite.getPlugin().getDataFolder(), "bimapdb");
            try {
                db.createNewFile();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
                    oos.writeObject(this);
                }
            } catch (IOException e) {
                Msg.log("Something went very wrong when saving db.");
                e.printStackTrace();
            }
        });
    }

    public static <K, V> BiMap<K, V> load() {
        final File db = new File(Invite.getPlugin().getDataFolder(), "bimapdb");
        try {
            if (db.createNewFile()) {
                return new BiMap<>();
            } else {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
                    return (BiMap<K, V>) ois.readObject();
                }
            }
        } catch (IOException | RuntimeException | ClassNotFoundException e) {
            Msg.log("Something went very wrong when loading db.");
            e.printStackTrace();
        }
        return new BiMap<>();
    }
}