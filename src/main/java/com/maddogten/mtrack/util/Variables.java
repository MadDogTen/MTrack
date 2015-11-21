package com.maddogten.mtrack.util;

import java.io.File;

@SuppressWarnings("ClassWithoutLogger")
public class Variables {
    //---------- Main Variables ----------\\ -- "/**/" == Dev Option - Set to normal state for builds (Comment on the right).
    public static final short SIZE_WIDTH = 282;
    public static final short SIZE_HEIGHT = 454;
    public static final short SHOWS_COLUMN_WIDTH = 239;
    public static final short REMAINING_COLUMN_WIDTH = 29;
    public static final short SEASONS_COLUMN_WIDTH = 48;
    public static final short EPISODE_COLUMN_WIDTH = 50;

    // Base Directories
    public static final String ProgramRootFolder = Strings.FileSeparator + "MTrack";
    public static final String DirectoriesFolder = Strings.FileSeparator + "Directories";
    public static final String UsersFolder = Strings.FileSeparator + "Users";
    // Extensions
    public static final String ShowsExtension = ".shows";
    public static final String UsersExtension = ".user";
    public static final String SettingsExtension = ".settings";
    public static final String DefaultLanguage = "en_US";
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- // TODO Set back to 1 for full release. \\
    public static final int ProgramSettingsFileVersion = 1010; // Was Changed //TODO Remove all "Was Changed" before merging with master.
    public static final int UserSettingsFileVersion = 1002; // Was Changed
    public static final int ShowFileVersion = 1000; // Was Changed

    /**/public static final boolean showOptionToToggleDevMode = false; // false
    /**/public static final boolean startFresh = false; // false -- Won't work unless devMode is true.
    public static final int defaultUpdateSpeed = 120;
    public static final int defaultTimeToWaitForDirectory = 20;
    public static final int maxWaitTimeSeconds = 172800; // 2 Days will be the max wait time - Might be changed
    public static final String Logo = "/image/MTrackLogo.png";
    /**/public static boolean devMode = false; // false
    public static int updateSpeed;
    public static File dataFolder = new File(Strings.EmptyString);
    public static String language;
    public static int timeToWaitForDirectory;

    //---------- Other Variables ----------\\

    public static void setUpdateSpeed(int updateSpeed) {
        Variables.updateSpeed = updateSpeed;
    }

    public static void setTimeToWaitForDirectory(int timeToWaitForDirectory) {
        Variables.timeToWaitForDirectory = timeToWaitForDirectory;
    }

    public static void setDataFolder(File file) {
        dataFolder = file;
    }

    //---------- Temp Variables ----------\\
}