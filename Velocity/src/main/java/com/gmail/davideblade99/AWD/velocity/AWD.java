/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Plugin(id = AWDInfo.ID, name = AWDInfo.NAME, version = AWDInfo.VERSION, url = AWDInfo.WEBSITE, description = AWDInfo.DESCRIPTION, authors = {AWDInfo.AUTHOR})
public final class AWD {

    public static final LegacyChannelIdentifier OLD_CHANNEL = new LegacyChannelIdentifier("wdl|init"); // Up to 1.12
    public static final MinecraftChannelIdentifier CHANNEL_1_13 = MinecraftChannelIdentifier.from("wdl:init"); // From 1.13

    private final static String PREFIX = "&e[&c&lAntiWorldDownloader&e] ";
    private static AWD instance;

    private final ProxyServer proxy;
    private final Path pluginDirectory;

    private ConfigurationNode config;
    private ConfigurationNode messages;

    @Inject
    public AWD(final ProxyServer proxy, @DataDirectory final Path dataDirectory) {
        this.proxy = proxy;
        this.pluginDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        new Updater(this).checkForUpdate(newVersion -> {
            sendMessage(proxy.getConsoleCommandSource(), "&eFound a new version: " + newVersion + " (Yours: v" + AWDInfo.VERSION + ")");
            sendMessage(proxy.getConsoleCommandSource(), "&eDownload it on spigot:");
            sendMessage(proxy.getConsoleCommandSource(), "&espigotmc.org/resources/99356");
        });

        try {
            loadConfig();
            loadMessages();
        } catch (final ConfigurateException e) {
            e.printStackTrace();
            return;
        }

        instance = this;

        registerCommands();
        registerChannels();

        sendMessage(proxy.getConsoleCommandSource(), "&eAWD has been enabled! (Version: " + AWDInfo.VERSION + ")");
    }

    @Subscribe
    public void onShutdown(final ProxyShutdownEvent event) {
        proxy.getChannelRegistrar().unregister(CHANNEL_1_13);
        proxy.getChannelRegistrar().unregister(OLD_CHANNEL);

        instance = null;

        sendMessage(proxy.getConsoleCommandSource(), "&eAWD has been disabled! (Version: " + AWDInfo.VERSION + ")");
    }

    @Subscribe
    public void onPluginMessageFromPlayer(final PluginMessageEvent event) {
        if (!event.getIdentifier().equals(OLD_CHANNEL) && !event.getIdentifier().equals(CHANNEL_1_13))
            return;
        if (!(event.getSource() instanceof Player))
            return;

        final Player player = (Player) event.getSource();
        if (!player.hasPermission("antiwdl.allow")) {
            try {
                final Boolean executionResult = proxy.getCommandManager().executeAsync(proxy.getConsoleCommandSource(), config.node("Command").getString("").replace("%player", player.getUsername()).replace("&", "§")).get();

                if (executionResult) {
                    if (config.node("Debug").getBoolean(false))
                        sendMessage(proxy.getConsoleCommandSource(), getMessage("Player rejected").replace("%player", player.getUsername()));
                } else
                    sendMessage(proxy.getConsoleCommandSource(), getMessage("Unknown command"));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else if (config.node("Debug").getBoolean(false))
            sendMessage(proxy.getConsoleCommandSource(), getMessage("Player joined").replace("%player", player.getUsername()));
    }

    public String getMessage(final String path) {
        return messages.getString(path);
    }

    public void sendMessage(final Audience receiver, final String message) {
        receiver.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(PREFIX + message));
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    private void registerCommands() {
        final CommandManager commandManager = proxy.getCommandManager();
        final CommandMeta commandMeta = commandManager.metaBuilder("AWD")
                .plugin(this)
                .build();

        commandManager.register(commandMeta, com.gmail.davideblade99.AWD.velocity.command.AWD.createAWDCommand(this));
    }

    private void registerChannels() {
        proxy.getChannelRegistrar().register(CHANNEL_1_13);
        proxy.getChannelRegistrar().register(OLD_CHANNEL);
    }

    /**
     * Create if it does not exist and then load the configuration file
     */
    private void loadConfig() throws ConfigurateException {
        final File configFile = pluginDirectory.resolve("config.yml").toFile();
        if (!configFile.exists())
            copyFile("config.yml", configFile);

        try {
            config = YamlConfigurationLoader.builder().path(configFile.toPath()).build().load();
        } catch (final ConfigurateException e) {
            sendMessage(proxy.getConsoleCommandSource(), "&cFailed to load config.yml.");

            throw e;
        }
    }

    private void loadMessages() throws ConfigurateException {
        final String extension = config.node("Locale").getString("en");
        final File messagesFile = Paths.get(pluginDirectory.toString(), "Messages", "messages_" + extension + ".yml").toFile();

        if (!messagesFile.exists())
            copyFile("messages_" + extension + ".yml", messagesFile);

        try {
            messages = YamlConfigurationLoader.builder().path(messagesFile.toPath()).build().load();
        } catch (final ConfigurateException e) {
            sendMessage(proxy.getConsoleCommandSource(), "&cFailed to load messages_" + extension + ".yml.");

            throw e;
        }
    }

    public static AWD getInstance() {
        return instance;
    }

    /**
     * Copy a file embedded into the .jar to another location
     *
     * @param inFile  The name of embedded file to be copied
     * @param outFile The file where specified embedded file should be copied
     */
    private static void copyFile(final String inFile, final File outFile) {
        outFile.getParentFile().mkdirs(); // Create output folder

        // Copy contents of inFile to outFile
        try (final InputStream input = AWD.class.getClassLoader().getResourceAsStream(inFile); final OutputStream output = new FileOutputStream(outFile)) {
            final byte[] buf = new byte[1024];
            int number;
            while ((number = input.read(buf)) > 0)
                output.write(buf, 0, number);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}