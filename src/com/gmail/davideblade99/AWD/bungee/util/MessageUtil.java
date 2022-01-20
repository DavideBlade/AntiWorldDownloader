/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bungee.util;

import com.gmail.davideblade99.AWD.bungee.AWD;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public final class MessageUtil {

    private final static String PREFIX = "§e[§c§lAntiWorldDownloader§e] ";

    private MessageUtil() {
        throw new IllegalAccessError();
    }

    public static void sendMessage(final CommandSender receiver, final String message) {
        receiver.sendMessage(new TextComponent(MessageUtil.PREFIX + ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void sendMessage(final String message) {
        sendMessage(AWD.getInstance().getProxy().getConsole(), message);
    }
}