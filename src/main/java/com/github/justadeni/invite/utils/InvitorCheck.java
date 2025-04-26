package com.github.justadeni.invite.utils;

import com.github.justadeni.invite.config.Config;
import com.github.justadeni.invite.db.Invitor;

public sealed interface InvitorCheck permits InvitorCheck.CanInvite, InvitorCheck.Timeout {

    final class CanInvite implements InvitorCheck {}
    record Timeout(String timeleftstring) implements InvitorCheck {}

    static InvitorCheck get(Invitor invitor) {
        if (invitor.getInvitesLeft() <= 0) {
            long timeleft = System.currentTimeMillis() - invitor.getLastInvited();
            if (timeleft < Config.getInstance().TIMEOUT) {
                long totalSecs = timeleft / 1000;
                int hours = (int) totalSecs / 3600;
                int minutes = (int) (totalSecs % 3600) / 60;
                return new Timeout(hours + "h " + minutes + "m");
            }
        }
        return new CanInvite();
    }

}
