package com.github.justadeni.invite.db;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.utils.Msg;

import java.io.*;

public class Database {

    private static BiMap<Invitor, String> db;

    public static BiMap<Invitor, String> get() {
        if (db == null)
            db = load();

        return db;
    }

    public static void save() {
        Thread.ofVirtual().start(() -> {
            final File db = new File(Invite.getPlugin().getDataFolder(), "bimapdb");
            try {
                db.createNewFile();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
                    oos.writeObject(db);
                }
            } catch (IOException e) {
                Msg.log("Something went very wrong when saving db.");
                e.printStackTrace();
            }
        });
    }

    private static <K, V> BiMap<K, V> load() {
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
