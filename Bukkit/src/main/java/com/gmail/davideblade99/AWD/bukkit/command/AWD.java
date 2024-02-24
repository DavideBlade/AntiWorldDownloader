/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bukkit.command;

import com.gmail.davideblade99.AWD.bukkit.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class AWD implements CommandExecutor {

    private final com.gmail.davideblade99.AWD.bukkit.AWD plugin;

    public AWD(final com.gmail.davideblade99.AWD.bukkit.AWD instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            final String[] message = {
                    "§e§m------------[§c§lAWD§e§m]------------",
                    "§3Developer: §eDavideBlade",
                    "§3Version: §e" + plugin.getDescription().getVersion(),
                    "§3Help: §e/AWD help"
            };
            sender.sendMessage(message);
            return true;
        }


        if (args[0].equalsIgnoreCase("help")) {
            final String[] messages = {
                    "§e§m--------[§c§lAWD commands§e§m]--------",
                    "§3/AWD - §ePlugin information."
            };
            sender.sendMessage(messages);
            return true;
        }


        MessageUtil.sendMessage(sender, "&cUnknow sub-command \"" + args[0] + "\". Use /AWD help.");
        return true;
    }
}