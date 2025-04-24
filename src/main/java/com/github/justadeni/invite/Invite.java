package com.github.justadeni.invite;

import com.github.justadeni.invite.db.Database;
import com.github.justadeni.invite.invited.TreeManager;
import com.github.justadeni.invite.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

public final class Invite extends JavaPlugin {

    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        if (Config.getInstance().OFFLINE_SUGGESTIONS)
            Thread.ofVirtual().start(TreeManager::downloadAndBuild);
    }

    public void onDisable() {
        Database.save();
    }

}
