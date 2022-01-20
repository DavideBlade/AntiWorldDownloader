/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.AWD.bukkit.util;

import com.gmail.davideblade99.AWD.bukkit.AWD;

import java.io.*;

public final class FileUtil {

    public final static File CONFIG_FILE = new File(AWD.getInstance().getDataFolder(), "config.yml");

    private FileUtil() {
        throw new IllegalAccessError();
    }

    /**
     * Copy an embedded file to another location
     *
     * @param inFile - the name of embedded file to be copied
     * @param outFile - the file where specified embedded file should be copied
     */
    public static void copyFile(final String inFile, final File outFile) {
        outFile.getParentFile().mkdirs(); // Create output folder

        // Copy contents of inFile to outFile
        try (final InputStream input = AWD.getInstance().getResource(inFile); final OutputStream output = new FileOutputStream(outFile)) {
            final byte[] buf = new byte[1024];
            int number;
            while ((number = input.read(buf)) > 0)
                output.write(buf, 0, number);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}