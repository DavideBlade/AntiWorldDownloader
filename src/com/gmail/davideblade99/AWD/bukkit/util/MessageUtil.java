/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public final class MessageUtil {

    private final static String PREFIX = "§e[§c§lAntiWorldDownloader§e] ";
    private final static ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    private MessageUtil() {
        throw new IllegalAccessError();
    }

    public static void sendMessage(final CommandSender receiver, final String message) {
        receiver.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(final String message) {
        sendMessage(CONSOLE, message);
    }
}