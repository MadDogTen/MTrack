package com.maddogten.mtrack.util;

public class StringDB {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String DBFolderName = "MTrackDB";
    public static final int directoryLength = 2048;


    // Tables
    public static final String shows = "shows";
    public static final String seasons = "seasons";
    public static final String episodes = "episodes";
    public static final String users = "users";
    public static final String settings = "settings";
    public static final String directories = "directories";
    public static final String episodeFiles = "episodeFiles";
    public static final String programSettings = "programSettings";

    public static final String showsInDirectory = "showsInDirectory";
    public static final String userShowSettings = "userShowSettings";
    public static final String userEpisodeSettings = "userEpisodeSettings";


    // Fields
    public static final String name = "name";
    public static final String username = "username";
    public static final String userID = "userID";
    public static final String showID = "showID";
    public static final String episodeID = "episodeID";
    public static final String directoryID = "directoryID";
    public static final String directoryPriority = "directoryPriority";

    // -- Settings -- \\
    public static final String showUsername = "showUsername";
    public static final String updateSpeed = "updateSpeed";
    public static final String automaticShowUpdating = "automaticShowUpdating";
    public static final String timeToWaitForDirectory = "timeToWaitForDirectory";
    public static final String show0Remaining = "show0Remaining";
    public static final String showActiveShows = "showActiveShows";
    public static final String language = "language";
    public static final String recordChangesForNonActiveShows = "recordChangesForNonActiveShows";
    public static final String recordChangedSeasonsLowerThanCurrent = "recordChangedSeasonsLowerThanCurrent";
    public static final String moveStageWithParent = "moveStageWithParent";
    public static final String haveStageBlockParentStage = "haveStageBlockParentStage";
    public static final String enableSpecialEffects = "enableSpecialEffects";
    public static final String enableAutomaticSaving = "enableAutomaticSaving";
    public static final String saveSpeed = "saveSpeed";
    public static final String enableFileLogging = "enableFileLogging";
    public static final String useRemoteDatabase = "useRemoteDatabase";
    public static final String showColumnWidth = "showColumnWidth";
    public static final String remainingColumnWidth = "remainingColumnWidth";
    public static final String seasonColumnWidth = "seasonColumnWidth";
    public static final String episodeColumnWidth = "episodeColumnWidth";
    public static final String showColumnVisibility = "showColumnVisibility";
    public static final String remainingColumnVisibility = "remainingColumnVisibility";
    public static final String seasonColumnVisibility = "seasonColumnVisibility";
    public static final String episodeColumnVisibility = "episodeColumnVisibility";

    // -- User Show Settings -- \\
    public static final String currentSeason = "currentSeason";
    public static final String currentEpisode = "currentEpisode";
    public static final String active = "active";
    public static final String ignored = "ignored";
    public static final String hidden = "hidden";

    // -- User Episode Settings -- \\
    public static final String episodeTimePosition = "episodeTimePosition";

    // -- Show -- \\
    public static final String showName = "showName";
    public static final String season = "season";
    public static final String episode = "episode";
    public static final String file = "file";
    public static final String partOfDoubleEpisode = "partOfDoubleEpisode";

    // -- Directory -- \\
    public static final String directory = "directory";

    // -- Program Settings -- \\
    public static final String defaultUser = "defaultUser";
    public static final String programSettingsTableVersion = "programSettingsTableVersion";
    public static final String settingsTableVersion = "userSettingsTableVersion";
    public static final String showsTableVersion = "showsTableVersion";
    public static final String seasonsTableVersion = "seasonsTableVersion";
    public static final String episodesTableVersion = "episodesTableVersion";
    public static final String episodeFilesTableVersion = "episodeFilesTableVersion";
    public static final String directoriesTableVersion = "directoriesTableVersion";
    public static final String usersTableVersion = "usersTableVersion";

}
