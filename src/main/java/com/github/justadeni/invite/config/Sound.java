package com.github.justadeni.invite.config;

import com.github.justadeni.invite.Invite;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sound {

    private final org.bukkit.Sound sound;
    private final Float volume;
    private final Float pitch;

    public Sound(String key) {
        this.sound = Registry.SOUND_EVENT.get(Key.key(Invite.getPlugin().getConfig().getString(key + ".key")));
        this.volume = (float) Invite.getPlugin().getConfig().getDouble(key + ".volume");
        this.pitch = (float) Invite.getPlugin().getConfig().getDouble(key + ".pitch");
    }

    public void play(Player player) {
        player.playSound(player, sound, volume, pitch);
    }

    public void play(CommandSender sender) {
        if (sender instanceof Player player)
            play(player);
    }

}
