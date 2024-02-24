/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.velocity;

/**
 * The templating-maven-plugin replaces the information obtained from Maven (pom) in this class, which is then used to
 * compile the {@link com.velocitypowered.api.plugin.Plugin} annotation
 */
public final class AWDInfo {
    public static final String ID = "${plugin.id}";
    public static final String NAME = "${plugin.name}";
    public static final String AUTHOR = "${plugin.author}";
    public static final String WEBSITE = "${plugin.website}";
    public static final String VERSION = "${project.version}";
    public static final String DESCRIPTION = "${project.description}";
}