package com.github.justadeni.invite.config;

import com.github.justadeni.invite.Invite;
import org.intellij.lang.annotations.Subst;

public class Config {

    private static Config instance;

    private Config () {}

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();

        return instance;
    }

    public static void reload() {
        Invite.getPlugin().reloadConfig();
        instance = null;
    }

    private static <T> T get(String key) {
        return (T) Invite.getPlugin().getConfig().get(key);
    }

    // Messages
    public final String PREFIX = get("messages.prefix");
    public final String NO_PERMISSION = get("messages.no-permission");
    public final String RELOADED = get("messages.reloaded");
    public final String INVITE_NONEXISTENT = get("messages.invite-nonexistent");
    public final String INVITE_INVITED = get("messages.invite-invited");
    public final String INVITE_PLAYING = get("messages.invite-playing");
    public final String INVITE_SUCCESS = get("messages.invite-success");
    public final String INVITE_TIMEOUT = get("messages.invite-timeout");
    public final String ONLY_PLAYER = get("messages.only-player");
    public final String UNINVITE_FAILURE = get("messages.uninvite-failure");
    public final String UNINVITE_SUCCESS = get("messages.uninvite-success");

    // Optimization settings
    public final boolean OFFLINE_SUGGESTIONS = get("offline-suggestions");
    public final int FIRST_N_SUGGESTIONS = get("first-n-suggestions");
    public final int CACHE_SURVIVAL = get("cache-survival");

    // Invite Settings
    public final int INSTANT_INVITES = get("instant-invites");
    public final int TIMEOUT = get("timeout");

    // Sounds
    public final Sound SOUND_FAILURE = new Sound("sound-failure");
    public final Sound SOUND_SUCCESS = new Sound("sound-success");

}
