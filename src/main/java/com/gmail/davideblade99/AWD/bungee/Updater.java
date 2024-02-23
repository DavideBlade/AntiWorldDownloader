/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bungee;

import com.gmail.davideblade99.AWD.bungee.util.MessageUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

final class Updater {

    public interface ResponseHandler {

        /**
         * Called when the updater finds a new version.
         *
         * @param newVersion - the new version
         */
        void onUpdateFound(final String newVersion);
    }

    private final AWD plugin;

    Updater(final AWD instance) {
        this.plugin = instance;
    }

    void checkForUpdate(final ResponseHandler responseHandler) {
        new Thread(() -> {
            try {
                final HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=99356").openConnection();
                final String newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();

                if (isNewerVersion(newVersion)) {
                    AWD.getInstance().getProxy().getScheduler().runAsync(AWD.getInstance(), () -> {
                        if (newVersion.contains(" "))
                            responseHandler.onUpdateFound(newVersion.split(" ")[0]);
                        else
                            responseHandler.onUpdateFound(newVersion);
                    });
                }
            }
            catch (final IOException ex) {
                MessageUtil.sendMessage("&cCould not contact Spigot to check for updates.");
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                MessageUtil.sendMessage("&cUnable to check for updates: unhandled exception.");
            }
        }).start();
    }

    /**
     * Compare the version found with the plugin's version
     */
    private boolean isNewerVersion(final String versionOnSpigot) {
        return !plugin.getDescription().getVersion().equals(versionOnSpigot);
    }
}