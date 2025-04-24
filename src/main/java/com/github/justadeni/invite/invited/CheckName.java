package com.github.justadeni.invite.invited;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class CheckName {

    public static boolean exists(String playerName) {
        try {
            String urlStr = String.format("https://api.mojang.com/users/profiles/minecraft/%s?at=%d", playerName, System.currentTimeMillis());
            URL url = new URI(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

}
