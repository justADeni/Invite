package com.github.justadeni.invite;

import com.github.justadeni.invite.autocomplete.TrieManager;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class Invite extends JavaPlugin implements PluginBootstrap {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(InviteCommand.createCommand("invite"));
        });
    }

    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Thread.ofVirtual().start(TrieManager::downloadAndBuild);
    }

    @Override
    public void onDisable() {

    }

    public static void log(String message) {
        if (plugin != null)
            plugin.getLogger().info(message);
    }

}
