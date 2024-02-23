/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public final class AWD extends Command {

    private final static TextComponent HELP_HEADER = new TextComponent("§e§m--------[§c§lAWD commands§e§m]--------");
    private final static TextComponent HELP_ROW_1 = new TextComponent("§3/AWD - §ePlugin information.");

    private final com.gmail.davideblade99.AWD.bungee.AWD plugin;

    public AWD(final com.gmail.davideblade99.AWD.bungee.AWD instance) {
        super("awd");

        this.plugin = instance;
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args) {
        if (args.length == 0) {
            final String[] messages = {
                    "§e§m------------[§c§lAWD§e§m]------------",
                    "§3Developer: §eDavideBlade",
                    "§3Version: §e" + plugin.getDescription().getVersion(),
                    "§3Help: §e/AWD help"
            };

            for (String message : messages)
                commandSender.sendMessage(new TextComponent(message));
            return;
        }


        if (args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(HELP_HEADER);
            commandSender.sendMessage(HELP_ROW_1);
            return;
        }


        commandSender.sendMessage(new TextComponent("§cUnknow sub-command \"" + args[0] + "\". Use /AWD help."));
    }
}