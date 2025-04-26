package com.github.justadeni.invite;

import com.github.justadeni.invite.db.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class Invite extends JavaPlugin {

    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
    }

    public void onDisable() {
        Database.get().save();
    }

}
