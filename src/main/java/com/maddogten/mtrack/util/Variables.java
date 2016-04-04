package com.maddogten.mtrack.util;

import javafx.scene.text.Font;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.regex.Pattern;

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
    public static final String TextExtension = ".txt";
    // Other
    public static final Font Font = javafx.scene.text.Font.font(("Times New Roman"));
    @SuppressWarnings("PublicStaticArrayField")
    public static final String[] DefaultLanguage = new String[]{"en_US", "English US"};
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- // Note- Set back to 1 for full release. \\
    public static final int ProgramSettingsFileVersion = 1015; // Was Changed // Note- Remove all "Was Changed" before merging with master.
    public static final int UserSettingsFileVersion = 1005; // Was Changed
    public static final int DirectoryFileVersion = 1001; // Was Changed

    public static final int InternalVersion = 35; // To help keep track of what I'm currently working on / testing.

    /**/public static final boolean showOptionToToggleDevMode = true; // false
    /**/public static final boolean startFresh = false; // false -- Won't work unless devMode is true.
    public static final boolean showInternalVersion = true; // Set to false or remove before full release
    public static final int defaultUpdateSpeed = 120;
    public static final int defaultTimeToWaitForDirectory = 20;
    public static final int defaultSavingSpeed = 600;
    public static final int maxWaitTimeSeconds = 172800; // 2 Days will be the max wait time - Might be changed
    public static final String Logo = "/image/MTrackLogo.png";
    public static final int checkAllNonIgnoredShowsInterval = 10; // May add user option to change these.
    public static final int checkSeasonsLowerThanCurrentInterval = 5;
    public static final int recheckPreviouslyFoundEmptyShowsInterval = 5;
    /**/public static final boolean forceDisableAutomaticRechecking = false; // false
    public static final boolean genUserShowInfoAtFirstFound = false; // Add to user settings // Add choice when creating user
    //---------- Episode Variables ----------\\
    @SuppressWarnings("PublicStaticCollectionField")
    public static final ArrayList<Pattern> doubleEpisodePatterns = new ArrayList<>(Collections.singletonList(Pattern.compile("s\\d{1,4}e(\\d{1,4})[e|-](\\d{1,4})")));
    @SuppressWarnings("PublicStaticCollectionField")
    public static final ArrayList<Pattern> singleEpisodePatterns = new ArrayList<>(Arrays.asList(Pattern.compile("s\\d{1,4}e(\\d{1,4})"), Pattern.compile("episode\\s(\\d{1,4})"), Pattern.compile("\\d{1,4}\\s?x\\s?(\\d{1,4})")));
    public static final String fileNameReplace = "[(]|[)]|[\\\\]|[\\[]]|[\\]]|[\\[]|[\\]]|[+][\\{][\\}]";
    public static final String LogsFolder = Strings.FileSeparator + "Logs";
    public final static String findShowURL = "http://api.tvmaze.com/singlesearch/shows?q=";
    public final static String getShowWithID = "http://api.tvmaze.com/shows/";
    public final static String episodesAddition = "/episodes";
    public static final Level loggingLevel = Level.FINEST;
    @SuppressWarnings("PublicStaticArrayField")
    static final String[] showExtensions = new String[]{".mkv", ".avi", ".mp4", ".ts"};
    static final int logMaxFileSize = 1000000;
    static final int logMaxNumberOfFiles = 10;
    public static boolean disableAutomaticRechecking;
    /**/public static boolean devMode = false; // false
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
    public static boolean specialEffects;
    public static int savingSpeed;
    public static boolean enableAutoSavingOnTimer;
    @SuppressWarnings("CanBeFinal")
    public static boolean enableFileLogging;
    @SuppressWarnings("CanBeFinal")
    public static boolean showActiveShows;
    public static boolean show0Remaining;
    //---------- TV Maze ----------\\
    @SuppressWarnings("CanBeFinal")
    public static boolean useOnlineDatabase; // TODO Still highly unfinished.

    public static void setDataFolder(File file) {
        dataFolder = file;
    }

    public static void setStageMoveWithParentAndBlockParent(boolean stageMoveWithParentAndBlockParent) {
        moveStageWithParent = stageMoveWithParentAndBlockParent;
        haveStageBlockParentStage = stageMoveWithParentAndBlockParent;
    }


    public enum OperatingSystem {
        WINDOWS, MAC, NIX, NUX, AIX
    }

    public enum ShowColorStatus {
        REMOVED("LightCoral"), ADDED("DeepSkyBlue"), DEFAULT("LimeGreen"), ACTIVE("LightGrey");

        private final String color;

        ShowColorStatus(String color) {
            this.color = color;
        }

        public static ShowColorStatus findColorFromRemaining(int previouslyRemaining, int currentlyRemaining) {
            if (previouslyRemaining < currentlyRemaining) return ADDED;
            else if (previouslyRemaining > currentlyRemaining) return REMOVED;
            else return DEFAULT;
        }

        public String getColor() {
            return color;
        }
    }
}
