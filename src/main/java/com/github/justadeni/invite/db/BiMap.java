package com.github.justadeni.invite.db;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.utils.Msg;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BiMap<K, V> implements Serializable {
    BiMap() {}

    private final Map<K, Set<V>> map = new ConcurrentHashMap<>();

    public void put(K key, V value) {
        if (map.containsKey(key))
            map.get(key).add(value);
        else
            map.put(key, new HashSet<>(List.of(value)));
    }

    public Set<V> get(K key) {
        Set<V> set = map.get(key);
        if (set == null)
            return Set.of();
        return set;
    }

    public Set<V> getForward(K key) {
        return map.getOrDefault(key, Collections.emptySet());
    }

    public Set<K> getKeys() {
        return map.keySet();
    }

    public Set<V> getValues() {
        return map.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public int countValues(K key) {
        return map.getOrDefault(key, Collections.emptySet()).size();
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