package com.github.justadeni.invite.db;

import java.util.Set;
import java.util.UUID;

public class Database {

    private static BiMap<Invitor, String> db = null;

    public static BiMap<Invitor, String> get() {
        if (db == null)
            db = BiMap.load();

        return db;
    }

    public static Set<String> getByPlayer(UUID uuid) {
        for (Invitor invitor : get().getKeys()) {
            if (invitor.getUUID() == uuid) {
                return get().get(invitor);
            }
        }
        return Set.of();
    }

}
