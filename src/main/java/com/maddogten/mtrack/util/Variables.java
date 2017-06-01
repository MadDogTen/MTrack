package com.maddogten.mtrack.util;

import javafx.scene.text.Font;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

/*
 *    Variables holds the variables for various things, Most variable should be put into here.
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
    public static final String DirectoriesFolder = Strings.FileSeparator + "Directories";
    public static final String UsersFolder = Strings.FileSeparator + "Users";
    // Extensions
    public static final String ShowFileExtension = ".shows";
    public static final String UserFileExtension = ".user";
    public static final String SettingFileExtension = ".settings";
    public static final String TempExtension = ".temp";
    // Other
    public static final Font Font = javafx.scene.text.Font.font(("Times New Roman"));
    @SuppressWarnings("PublicStaticArrayField")
    public static final String[] DefaultLanguage = new String[]{"en_US", "English US"};
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- // Note- Set back to 1 for full release. \\
    public static final int ProgramSettingsFileVersion = 1015; // Was Changed // Note- Remove all "Was Changed" before merging with master.
    public static final int UserSettingsFileVersion = 1008; // Was Changed
    public static final int DirectoryFileVersion = 1004; // Was Changed
    public static final int defaultUpdateSpeed = 120;
    public static final int defaultTimeToWaitForDirectory = 20;
    public static final int defaultSavingSpeed = 600;
    public static final int maxWaitTimeSeconds = 172800; // 2 Days will be the max wait time - Might be changed
    public static final int timeBetweenRecheckingActiveDirectoriesSeconds = 2;
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
    public static final ArrayList<Pattern> singleEpisodePatterns = new ArrayList<>(Arrays.asList(Pattern.compile("s\\d{1,4}e(\\d{1,4})"), Pattern.compile("episode\\s(\\d{1,4})"), Pattern.compile("\\d{1,4}\\s?x\\s?(\\d{1,4})"), Pattern.compile("(\\d{1,4})")));
    public static final String fileNameReplace = "[(]|[)]|[\\\\]|[\\[]]|[\\]]|[\\[]|[\\]]|[+][\\{][\\}]";
    public static final String LogsFolder = Strings.FileSeparator + "Logs";
    public static final Level loggerLevel = Level.INFO; // INFO
    public static final int sleepTimeDelay = 61;
    // Base Directories
    static final String ProgramRootFolder = Strings.FileSeparator + "MTrack";
    static final String LogExtension = ".log";
    final static String findShowURL = "http://api.tvmaze.com/singlesearch/shows?q=";
    final static String getShowWithID = "http://api.tvmaze.com/shows/";
    final static String episodesAddition = "/episodes";
    static final Map<VideoPlayer.VideoPlayerEnum, Set<File>> supportedVideoPlayers_Windows;
    static final boolean playFullScreen = false; // TODO Add user setting
    @SuppressWarnings("PublicStaticArrayField")
    static final String[] showExtensions = new String[]{".mkv", ".avi", ".mp4", ".ts"};
    static final int logMaxFileSize = 10000000;
    static final int logMaxNumberOfFiles = 10;
    public static boolean disableAutomaticRechecking;
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
    public static boolean show0Remaining; // TODO Re-add user toggle for this.
    //---------- TV Maze ----------\\
    @SuppressWarnings("CanBeFinal")
    public static boolean useOnlineDatabase; // TODO Still highly unfinished.

    static {
        supportedVideoPlayers_Windows = new HashMap<>();

        // VLC
        supportedVideoPlayers_Windows.put(VideoPlayer.VideoPlayerEnum.VLC, new HashSet<>());
        supportedVideoPlayers_Windows.get(VideoPlayer.VideoPlayerEnum.VLC).add(new File("C:" + Strings.FileSeparator + "Program Files (x86)" + Strings.FileSeparator + "VideoLAN" + Strings.FileSeparator + "VLC" + Strings.FileSeparator + "vlc.exe"));
        supportedVideoPlayers_Windows.get(VideoPlayer.VideoPlayerEnum.VLC).add(new File("C:" + Strings.FileSeparator + "Program Files" + Strings.FileSeparator + "VideoLAN" + Strings.FileSeparator + "VLC" + Strings.FileSeparator + "vlc.exe"));

        // BS Player
        supportedVideoPlayers_Windows.put(VideoPlayer.VideoPlayerEnum.BS_PLAYER, new HashSet<>());
        supportedVideoPlayers_Windows.get(VideoPlayer.VideoPlayerEnum.BS_PLAYER).add(new File("C:" + Strings.FileSeparator + "Program Files (x86)" + Strings.FileSeparator + "Webteh" + Strings.FileSeparator + "BSPlayer" + Strings.FileSeparator + "bsplayer.exe"));

        // MPC
        supportedVideoPlayers_Windows.put(VideoPlayer.VideoPlayerEnum.MEDIA_PLAYER_CLASSIC, new HashSet<>());
        supportedVideoPlayers_Windows.get(VideoPlayer.VideoPlayerEnum.MEDIA_PLAYER_CLASSIC).add(new File("C:" + Strings.FileSeparator + "Program Files" + Strings.FileSeparator + "MPC-HC" + Strings.FileSeparator + "mpc-hc64.exe"));

    }

    public static void setDataFolder(File file) {
        dataFolder = file;
    }

    public static void setStageMoveWithParentAndBlockParent(boolean stageMoveWithParentAndBlockParent) {
        moveStageWithParent = stageMoveWithParentAndBlockParent;
        haveStageBlockParentStage = stageMoveWithParentAndBlockParent;
    }

    public enum ShowColorStatus {
        REMOVED("LightCoral"), ADDED("DeepSkyBlue"), DEFAULT("LimeGreen"), ACTIVE("LightGrey");

        private final String color;

        ShowColorStatus(final String color) {
            this.color = color;
        }

        public static ShowColorStatus findColorFromRemaining(final int previouslyRemaining, final int currentlyRemaining) {
            if (previouslyRemaining < currentlyRemaining) {
                return previouslyRemaining == -2 && currentlyRemaining == 0 ? DEFAULT : ADDED;
            }
            else if (previouslyRemaining > currentlyRemaining) return REMOVED;
            else return DEFAULT;
        }

        public String getColor() {
            return color;
        }
    }

}
