/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bungee;

import com.gmail.davideblade99.AWD.bungee.util.MessageUtil;
import com.gmail.davideblade99.AWD.bungee.util.FileUtil;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;

public final class AWD extends Plugin implements Listener {

    private final static String CHANNEL_1_13 = "wdl:init"; // From 1.13
    private final static String OLD_CHANNEL = "wdl|init"; // Up to 1.12

    private static Configuration messages;
    private static AWD instance;

    private String extension;
    private boolean isDebugging;
    private String command;

    @Override
    public void onEnable() {
        final String serverVersion = getProxy().getVersion();
        final String pluginVersion = getDescription().getVersion();

        instance = this;

        new Updater(this).checkForUpdate(newVersion -> {
            final String currentVersion = pluginVersion.contains(" ") ? pluginVersion.split(" ")[0] : pluginVersion;

            MessageUtil.sendMessage("&eFound a new version: " + newVersion + " (Yours: v" + currentVersion + ")");
            MessageUtil.sendMessage("&eDownload it on spigot:");
            MessageUtil.sendMessage("&espigotmc.org/resources/99356");
        });

        try {
            createConfig();
            loadMessages();
        }
        catch (final IOException ignored) {
            return;
        }

        if (!serverVersion.contains("1.13"))
            getProxy().registerChannel(OLD_CHANNEL);
        getProxy().registerChannel(CHANNEL_1_13);

        getProxy().getPluginManager().registerCommand(this, new com.gmail.davideblade99.AWD.bungee.command.AWD(this));
        getProxy().getPluginManager().registerListener(this, this);

        MessageUtil.sendMessage("&eAWD has been enabled! (Version: " + pluginVersion + ")");
    }

    @Override
    public void onDisable() {
        if (!getProxy().getVersion().contains("1.13"))
            getProxy().unregisterChannel(OLD_CHANNEL);
        getProxy().unregisterChannel(CHANNEL_1_13);

        MessageUtil.sendMessage("&eAWD has been disabled! (Version: " + getDescription().getVersion() + ")");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        final Connection sender = event.getSender();
        if (!event.getTag().equalsIgnoreCase(OLD_CHANNEL) && !event.getTag().equalsIgnoreCase(CHANNEL_1_13) || !(sender instanceof ProxiedPlayer))
            return;

        final ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("antiwdl.allow")) {
            if (getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command.replace("%player", player.getName()).replace("&", "§"))) {
                if (isDebugging)
                    MessageUtil.sendMessage(getMessage("Player rejected").replace("%player", player.getName()));
            } else
                MessageUtil.sendMessage(getMessage("Unknown command"));
        } else if (isDebugging)
            MessageUtil.sendMessage(getMessage("Player joined").replace("%player", player.getName()));
    }

    /**
     * Create config file
     */
    private void createConfig() throws IOException {
        final File configFile = FileUtil.CONFIG_FILE;
        if (!configFile.exists())
            FileUtil.copyFile("config.yml", configFile);


        // Load settings
        final Configuration settings;
        try {
            settings = ConfigurationProvider.getProvider(YamlConfiguration.class).load(FileUtil.CONFIG_FILE);
        }
        catch (final Exception e) {
            MessageUtil.sendMessage("&cFailed to load config.yml.");

            throw e;
        }

        extension = settings.getString("Locale", "en");
        isDebugging = settings.getBoolean("Debug", false);
        command = settings.getString("Command", "kick %player &4&lWorldDownloader is forbidden here!");
    }

    private void loadMessages() throws IOException {
        final File messagesFile = new File(getDataFolder() + "/Messages", "messages_" + extension + ".yml");
        if (!messagesFile.exists())
            FileUtil.copyFile("messages_" + extension + ".yml", messagesFile);

        try {
            messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesFile);
        }
        catch (final Exception e) {
            MessageUtil.sendMessage("&cFailed to load messages_" + extension + ".yml.");

            throw e;
        }
    }

    private String getMessage(final String path) {
        return messages.getString(path);
    }

    public static AWD getInstance() {
        return instance;
    }
}