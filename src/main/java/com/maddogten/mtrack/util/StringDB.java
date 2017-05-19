package com.maddogten.mtrack.util;

public class StringDB {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String DBFolderName = "MTrackDB";
    public static final int directoryLength = 2048;
    // Tables
    public static final String TABLE_SHOWS = "shows";
    public static final String TABLE_SEASONS = "seasons";
    public static final String TABLE_EPISODES = "episodes";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_USERSETTINGS = "userSettings";
    public static final String TABLE_DIRECTORIES = "directories";
    public static final String TABLE_EPISODEFILES = "episodeFiles";
    public static final String TABLE_PROGRAMSETTINGS = "programSettings";
    public static final String TABLE_SHOWSINDIRECTORY = "showsInDirectory";
    public static final String TABLE_USERSHOWSETTINGS = "userShowSettings";
    public static final String TABLE_USEREPISODESETTINGS = "userEpisodeSettings";
    // Columns
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USER_ID = "userID";
    public static final String COLUMN_SHOW_ID = "showID";
    public static final String COLUMN_EPISODE_ID = "episodeID";
    public static final String COLUMN_DIRECTORY_ID = "directoryID";
    public static final String COLUMN_DIRECTORYPRIORITY = "directoryPriority";
    // -- Settings -- \\
    public static final String COLUMN_SHOWUSERNAME = "showUsername";
    public static final String COLUMN_UPDATESPEED = "updateSpeed";
    public static final String COLUMN_AUTOMATICSHOWUPDATING = "automaticShowUpdating";
    public static final String COLUMN_TIMETOWAITFORDIRECTORY = "timeToWaitForDirectory";
    public static final String COLUMN_SHOW0REMAINING = "show0Remaining";
    public static final String COLUMN_SHOWACTIVESHOWS = "showActiveShows";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_RECORDCHANGESFORNONACTIVESHOWS = "recordChangesForNonActiveShows";
    public static final String COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT = "recordChangedSeasonsLowerThanCurrent";
    public static final String COLUMN_MOVESTAGEWITHPARENT = "moveStageWithParent";
    public static final String COLUMN_HAVESTAGEBLOCKPARENTSTAGE = "haveStageBlockParentStage";
    public static final String COLUMN_ENABLESPECIALEFFECTS = "enableSpecialEffects";
    public static final String COLUMN_ENABLEAUTOMATICSAVING = "enableAutomaticSaving";
    public static final String COLUMN_SAVESPEED = "saveSpeed";
    public static final String COLUMN_ENABLEFILELOGGING = "enableFileLogging";
    public static final String COLUMN_USEREMOTEDATABASE = "useRemoteDatabase";
    public static final String COLUMN_SHOWCOLUMNWIDTH = "showColumnWidth";
    public static final String COLUMN_REMAININGCOLUMNWIDTH = "remainingColumnWidth";
    public static final String COLUMN_SEASONCOLUMNWIDTH = "seasonColumnWidth";
    public static final String COLUMN_EPISODECOLUMNWIDTH = "episodeColumnWidth";
    public static final String COLUMN_SHOWCOLUMNVISIBILITY = "showColumnVisibility";
    public static final String COLUMN_REMAININGCOLUMNVISIBILITY = "remainingColumnVisibility";
    public static final String COLUMN_SEASONCOLUMNVISIBILITY = "seasonColumnVisibility";
    public static final String COLUMN_EPISODECOLUMNVISIBILITY = "episodeColumnVisibility";
    public static final String COLUMN_VIDEOPLAYERTYPE = "videoPlayer";
    public static final String COLUMN_VIDEOPLAYERLOCATION = "videoPlayerLocation";
    // -- User Show Settings -- \\
    public static final String COLUMN_CURRENTSEASON = "currentSeason";
    public static final String COLUMN_CURRENTEPISODE = "currentEpisode";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_IGNORED = "ignored";
    public static final String COLUMN_HIDDEN = "hidden";
    // -- User Episode Settings -- \\
    public static final String COLUMN_EPISODETIMEPOSITION = "episodeTimePosition";
    // -- Show -- \\
    public static final String COLUMN_SHOWNAME = "showName";
    public static final String COLUMN_SEASON = "season";
    public static final String COLUMN_EPISODE = "episode";
    public static final String COLUMN_FILE = "file";
    public static final String COLUMN_PARTOFDOUBLEEPISODE = "partOfDoubleEpisode";
    public static final String COLUMN_SHOWEXISTS = "showExists";
    // -- Directory -- \\
    public static final String COLUMN_DIRECTORY = "directory";
    public static final String COLUMN_DIRECTORYACTIVE = "active";
    // -- Program Settings -- \\
    public static final String COLUMN_DEFAULTUSER = "defaultUser";
    private static final String TABLEVERSION = "TableVersion";
    public static final String COLUMN_PROGRAMSETTINGSTABLEVERSION = TABLE_PROGRAMSETTINGS + TABLEVERSION;
    public static final String COLUMN_USERSETTINGSTABLEVERSION = TABLE_USERSETTINGS + TABLEVERSION;
    public static final String COLUMN_SHOWSTABLEVERSION = TABLE_SHOWS + TABLEVERSION;
    public static final String COLUMN_SEASONSTABLEVERSION = TABLE_SEASONS + TABLEVERSION;
    public static final String COLUMN_EPISODESTABLEVERSION = TABLE_EPISODES + TABLEVERSION;
    public static final String COLUMN_EPISODEFILESTABLEVERSION = TABLE_EPISODEFILES + TABLEVERSION;
    public static final String COLUMN_DIRECTORIESTABLEVERSION = TABLE_DIRECTORIES + TABLEVERSION;
    public static final String COLUMN_USERSTABLEVERSION = TABLE_USERS + TABLEVERSION;


}
