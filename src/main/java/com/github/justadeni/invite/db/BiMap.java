package com.github.justadeni.invite.db;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.utils.Msg;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BiMap<K, V> implements Serializable {
    BiMap() {}

    private final Map<K, Set<V>> map = new HashMap<>();

    public void put(K key, V value) {
        if (map.containsKey(key))
            map.get(key).add(value);
        else
            map.put(key, new HashSet<>(List.of(value)));
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

}