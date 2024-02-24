/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.velocity.command;

import com.gmail.davideblade99.AWD.velocity.AWDInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class AWD {

    private AWD() {
        throw new IllegalAccessError();
    }

    public static BrigadierCommand createAWDCommand(final com.gmail.davideblade99.AWD.velocity.AWD plugin) {
        final LiteralCommandNode<CommandSource> root = BrigadierCommand.literalArgumentBuilder("awd")
                .executes(context -> {
                    final CommandSource executor = context.getSource();

                    final String message = "&e&m------------[&c&lAWD&e&m]------------\n" +
                            "&3Developer: &eDavideBlade\n" +
                            "&3Version: &e" + AWDInfo.VERSION + "\n" +
                            "&3Help: &e/AWD help";
                    executor.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("argument", StringArgumentType.word())
                        .suggests((ctx, builder) -> builder.suggest("help").buildFuture())
                        .executes(context -> {
                            final CommandSource executor = context.getSource();
                            final String argumentProvided = context.getArgument("argument", String.class);

                            if (argumentProvided.equalsIgnoreCase("help")) {
                                final String message = "&e&m--------[&c&lAWD commands&e&m]--------\n" +
                                        "&3/AWD - &ePlugin information.";
                                plugin.sendMessage(executor, message);
                            } else
                                plugin.sendMessage(executor, "&cUnknow sub-command \"" + argumentProvided + "\". Use /AWD help.");

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

        return new BrigadierCommand(root);
    }
}