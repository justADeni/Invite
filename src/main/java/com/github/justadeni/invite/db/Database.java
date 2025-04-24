package com.github.justadeni.invite.db;

public class Database {

    private static BiMap<Invitor, String> db;

    public static BiMap<Invitor, String> get() {
        if (db == null)
            db = BiMap.load();

        return db;
    }

}
