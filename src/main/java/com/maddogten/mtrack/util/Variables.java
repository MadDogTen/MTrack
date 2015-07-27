package com.maddogten.mtrack.util;

import java.io.File;

@SuppressWarnings("ClassWithoutLogger")
public class Variables {
    //---------- Main Variables ----------\\
    public static final int SIZE_WIDTH = 282;
    public static final int SIZE_HEIGHT = 454;
    public static final int SHOWS_COLUMN_WIDTH = 239;
    public static final int REMAINING_COLUMN_WIDTH = 29;
    public static final int SEASONS_COLUMN_WIDTH = 48;
    public static final int EPISODE_COLUMN_WIDTH = 50;

    // Base Directories
    public static final String ProgramRootFolder = "/MTrack";
    public static final String DirectoriesFolder = "/Directories";
    public static final String UsersFolder = "/Users";
    // Extensions
    public static final String ShowsExtension = ".shows";
    public static final String UsersExtension = ".user";
    public static final String SettingsExtension = ".settings";
    public static final String DefaultLanguage = "en_US";
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- // TODO Set back to 1 for release. \\
    public static final int ProgramSettingsFileVersion = 1009; // Was Changed //TODO Remove all "Was Changed" before merging with master.
    public static final int UserSettingsFileVersion = 1002; // Was Changed
    public static final int ShowFileVersion = 1000; // Was Changed

    public static final boolean showOptionToToggleDevMode = true;
    public static final boolean startFresh = false; // Won't work unless devMode is true.
    public static final Integer defaultUpdateSpeed = 120;
    public static final String Logo = "/image/MTrackLogo.png";
    public static boolean devMode = false;
    public static Integer updateSpeed;
    public static File dataFolder = new File(Strings.EmptyString);
    public static String language;

    //---------- Other Variables ----------\\

    public static void setUpdateSpeed(int updateSpeed) {
        Variables.updateSpeed = updateSpeed;
    }

    public static void setDataFolder(File file) {
        dataFolder = file;
    }

    //---------- Temp Variables ----------\\
}