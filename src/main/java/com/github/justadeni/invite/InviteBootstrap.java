package com.github.justadeni.invite;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class InviteBootstrap implements PluginBootstrap {

    @SuppressWarnings("UnstableApiUsage")
    
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(InviteCommand.createCommand("invite"));
        });
    }

}
