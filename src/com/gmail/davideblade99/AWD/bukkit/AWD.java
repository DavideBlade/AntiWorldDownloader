/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bukkit;

import com.gmail.davideblade99.AWD.bukkit.util.MessageUtil;
import com.gmail.davideblade99.AWD.bukkit.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;

public final class AWD extends JavaPlugin implements PluginMessageListener {

    private final static String[] SUPPORTED_VERSIONS = {"1.8", "1.9", "1.10", "1.11", "1.12", "1.13", "1.14", "1.15", "1.16", "1.17", "1.18"};

    private final static FileConfiguration MESSAGES = new YamlConfiguration();

    private final static String CHANNEL_1_13 = "wdl:init"; // From 1.13
    private final static String OLD_CHANNEL = "wdl|init"; // Up to 1.12

    private static AWD instance;

    @Override
    public void onEnable() {
        final String pluginVersion = getDescription().getVersion();

        /*TODO: New link not yet known to the resource on Spigot
        new Updater(this).checkForUpdate(newVersion -> {
            final String currentVersion = pluginVersion.contains(" ") ? pluginVersion.split(" ")[0] : pluginVersion;

            ChatUtil.sendMessage("&eFound a new version: " + newVersion + " (Yours: v" + currentVersion + ")");
            ChatUtil.sendMessage("&eDownload it on spigot:");
            ChatUtil.sendMessage("&espigotmc.org/resources/23022");
        });*/

        if (!checkVersion()) {
            MessageUtil.sendMessage("&cThis version only supports the following versions:" + String.join(", ", SUPPORTED_VERSIONS));
            MessageUtil.sendMessage("&cAWD " + getDescription().getVersion() + " was disabled.");
            disablePlugin();
            return;
        }

        instance = this;

        createConfig();
        try {
            loadMessages();
        }
        catch (final InvalidConfigurationException ignored) {
            disablePlugin();
            return;
        }

        registerCommands();
        registerChannels();

        MessageUtil.sendMessage("&eAWD has been enabled! (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CHANNEL_1_13);

        try {
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this, OLD_CHANNEL);
        } catch (final Exception ignore) {
            // Couldn't unregister 1.12 plugin channels; this is normal on 1.13 and above
        }

        MessageUtil.sendMessage("&eAWD has been disabled! (Version: " + getDescription().getVersion() + ")");
    }

    public void reload() {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] data) {
        if (!channel.equalsIgnoreCase(OLD_CHANNEL) && !channel.equalsIgnoreCase(CHANNEL_1_13))
            return;

        if (!player.hasPermission("antiwdl.allow")) {
            if (Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("Command").replace("%player", player.getName()).replace("&", "§"))) {
                if (getConfig().getBoolean("Debug"))
                    MessageUtil.sendMessage(getMessage("Player rejected").replace("%player", player.getName()));
            } else
                MessageUtil.sendMessage(getMessage("Unknown command"));
        } else if (getConfig().getBoolean("Debug"))
            MessageUtil.sendMessage(getMessage("Player joined").replace("%player", player.getName()));
    }

    public String getMessage(final String path) {
        return MESSAGES.getString(path);
    }

    private void registerCommands() {
        getCommand("awd").setExecutor(new com.gmail.davideblade99.AWD.bukkit.command.AWD(this));
    }

    private void registerChannels() {
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL_1_13, this);

       try {
           Bukkit.getMessenger().registerIncomingPluginChannel(this, OLD_CHANNEL, this);
        } catch (final Exception ignore) {
           // Couldn't register 1.12 plugin channels; this is normal on 1.13 and above
       }
    }

    private void disablePlugin() {
        setEnabled(false);
    }

    /**
     * Create config file
     */
    private void createConfig() {
        final File configFile = FileUtil.CONFIG_FILE;
        if (!configFile.exists())
            FileUtil.copyFile("config.yml", configFile);
    }

    private void loadMessages() throws InvalidConfigurationException {
        final String extension = getConfig().getString("Locale");
        final File messagesFile = new File(getDataFolder() + "/Messages", "messages_" + extension + ".yml");

        if (!messagesFile.exists())
            FileUtil.copyFile("messages_" + extension + ".yml", messagesFile);

        try {
            MESSAGES.load(messagesFile);
        }
        catch (final Exception ignore) {
            MessageUtil.sendMessage("&cFailed to load messages_" + extension + ".yml.");

            throw new InvalidConfigurationException();
        }
    }

    /**
     * @return true if the Minecraft server version is supported, otherwise false
     */
    private boolean checkVersion() {
        final String serverVersion = Bukkit.getVersion();

        for (String version : SUPPORTED_VERSIONS)
            if (serverVersion.contains(version))
                return true;

        return false;
    }

    public static AWD getInstance() {
        return instance;
    }
}