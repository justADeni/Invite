package com.github.justadeni.invite.db;

import com.github.justadeni.invite.config.Config;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Invitor implements Serializable {

    private final UUID uuid;

    private long lastInvited;

    private int invitesLeft;

    public Invitor(UUID uuid) {
        this.uuid = uuid;
        this.lastInvited = 0;
        this.invitesLeft = Config.getInstance().INSTANT_INVITES;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getLastInvited() {
        return lastInvited;
    }

    public void setLastInvited() {
        this.lastInvited = System.currentTimeMillis();
    }

    public int getInvitesLeft() {
        return invitesLeft;
    }

    public void decreaseInvitesLeft() {
        if (invitesLeft > 0)
            this.invitesLeft--;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Invitor invitor)) return false;
        return Objects.equals(uuid, invitor.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
