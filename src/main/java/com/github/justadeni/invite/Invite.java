package com.github.justadeni.invite;

import com.github.justadeni.invite.autocomplete.TreeManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Invite extends JavaPlugin {

    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    
    public void onEnable() {
        plugin = this;
        Thread.ofVirtual().start(TreeManager::downloadAndBuild);
    }

    
    public void onDisable() {

    }

    public static void log(String message) {
        if (plugin != null)
            plugin.getLogger().info(message);
    }

}
