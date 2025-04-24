package com.github.justadeni.invite.utils;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.config.Config;
import org.bukkit.command.CommandSender;

public class Msg {

    public static void send(CommandSender recipient, String message) {
        recipient.sendMessage(Config.getInstance().PREFIX + message);
    }

    public static void log(String message) {
        Invite.getPlugin().getLogger().info(message);
    }

}
