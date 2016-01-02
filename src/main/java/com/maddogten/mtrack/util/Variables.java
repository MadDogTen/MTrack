package com.maddogten.mtrack.util;

import java.io.File;

/*
      Variables holds the variables for various things, Most variable should be put into here.
 */

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
    public static final String ShowFileExtension = ".shows";
    public static final String UserFileExtension = ".user";
    public static final String SettingFileExtension = ".settings";
    public static final String DefaultLanguage = "en_US";
    @SuppressWarnings("PublicStaticArrayField")
    public static final String[] showExtensions = new String[]{".mkv", ".avi", ".mp4", ".ts"};
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- // Note- Set back to 1 for full release. \\
    public static final int ProgramSettingsFileVersion = 1012; // Was Changed // Note- Remove all "Was Changed" before merging with master.
    public static final int UserSettingsFileVersion = 1003; // Was Changed
    public static final int DirectoryFileVersion = 1000; // Was Changed

    public static final int InternalVersion = 11; // To help keep track of what I'm currently working on / testing.

    /**/public static final boolean showOptionToToggleDevMode = true; // false
    /**/public static final boolean startFresh = false; // false -- Won't work unless devMode is true.
    public static final boolean showInternalVersion = true; // Set to false or remove before full release
    public static final int defaultUpdateSpeed = 120;
    public static final int defaultTimeToWaitForDirectory = 20;
    public static final int maxWaitTimeSeconds = 172800; // 2 Days will be the max wait time - Might be changed
    public static final String Logo = "/image/MTrackLogo.png";
    public static final int checkAllNonIgnoredShowsInterval = 10; // May add user option to change these.
    public static final int checkSeasonsLowerThanCurrentInterval = 5;
    public static final int recheckPreviouslyFoundEmptyShowsInterval = 5;
    /**/public static final boolean forceDisableAutomaticRechecking = false; // false
    public static boolean disableAutomaticRechecking;
    /**/public static boolean devMode = true; // false
    public static int updateSpeed;

    //---------- Other Variables ----------\\
    public static File dataFolder = new File(Strings.EmptyString);
    public static String language;
    public static boolean makeLanguageDefault;
    public static int timeToWaitForDirectory;
    public static boolean recordChangesForNonActiveShows;
    public static boolean recordChangedSeasonsLowerThanCurrent;
    public static boolean moveStageWithParent;
    public static boolean haveStageBlockParentStage;

    public static void setDataFolder(File file) {
        dataFolder = file;
    }

    public static void setStageMoveWithParentAndBlockParent(boolean stageMoveWithParentAndBlockParent) {
        moveStageWithParent = stageMoveWithParentAndBlockParent;
        haveStageBlockParentStage = stageMoveWithParentAndBlockParent;
    }

    //---------- Temp Variables ----------\\
}
