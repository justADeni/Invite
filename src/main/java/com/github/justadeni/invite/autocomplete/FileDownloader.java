package com.github.justadeni.invite.autocomplete;

import com.github.justadeni.invite.Invite;
import com.github.justadeni.invite.InviteCommand;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileDownloader {

    public sealed interface Result permits Result.Success, Result.Failure {
        final static class Success implements Result {}
        final static class Failure implements Result {}
    }

    public static Result downloadFile(String fileURL, String savePath) {
        try (BufferedInputStream in = new BufferedInputStream(new URI(fileURL).toURL().openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[8192];
            int bytesRead;
            int percent = 0;
            int i = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 8192)) != -1) {
                if (i >= 65750){
                    i = 0;
                    percent++;
                    Invite.log("Downloading player names, progress: " + percent + "%");
                }
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                i++;
            }
            Invite.log("Download finished, autocomplete will now be available for Invite plugin.");
            return new Result.Success();
        } catch (IOException | URISyntaxException e) {
            Invite.log("Download failed, are you offline? Autocomplete won't be available for Invite plugin.");
            return new Result.Failure();
        }
    }

}
