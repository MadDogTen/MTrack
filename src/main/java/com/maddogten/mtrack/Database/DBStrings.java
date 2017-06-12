package com.maddogten.mtrack.Database;

class DBStrings {
    static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    static final String DBFolderName = "MTrackDB";
    static final int directoryLength = 2048;
    // Tables
    static final String TABLE_SHOWS = "shows"; ////
    static final String TABLE_SEASONS = "seasons"; ////
    static final String TABLE_EPISODES = "episodes"; ////
    static final String TABLE_USERS = "users"; ////
    static final String TABLE_USERSETTINGS = "userSettings"; ////
    static final String TABLE_DIRECTORIES = "directories"; ////
    static final String TABLE_EPISODEFILES = "episodeFiles"; ////
    static final String TABLE_PROGRAMSETTINGS = "programSettings"; ////
    static final String TABLE_USERSHOWSETTINGS = "userShowSettings";
    static final String TABLE_USEREPISODESETTINGS = "userEpisodeSettings";
    static final String TABLE_SHOWCHANGES = "showChanges";
    static final String TABLE_USERCHANGETRACKING = "userChangeTracking";

    // Columns
    static final String COLUMN_NAME = "name";
    static final String COLUMN_USERNAME = "username";
    static final String COLUMN_USER_ID = "userID";
    static final String COLUMN_SHOW_ID = "showID";
    static final String COLUMN_EPISODE_ID = "episodeID";
    static final String COLUMN_DIRECTORY_ID = "directoryID";
    static final String COLUMN_DIRECTORYPRIORITY = "directoryPriority";
    // -- Settings -- \\
    static final String COLUMN_SHOWUSERNAME = "showUsername";
    static final String COLUMN_UPDATESPEED = "updateSpeed";
    static final String COLUMN_AUTOMATICSHOWUPDATING = "automaticShowUpdating";
    static final String COLUMN_TIMETOWAITFORDIRECTORY = "timeToWaitForDirectory";
    static final String COLUMN_SHOW0REMAINING = "show0Remaining";
    static final String COLUMN_SHOWACTIVESHOWS = "showActiveShows";
    static final String COLUMN_LANGUAGE = "language";
    static final String COLUMN_RECORDCHANGESFORNONACTIVESHOWS = "recordChangesForNonActiveShows";
    static final String COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT = "recordChangedSeasonsLowerThanCurrent";
    static final String COLUMN_MOVESTAGEWITHPARENT = "moveStageWithParent";
    static final String COLUMN_HAVESTAGEBLOCKPARENTSTAGE = "haveStageBlockParentStage";
    static final String COLUMN_ENABLESPECIALEFFECTS = "enableSpecialEffects";
    static final String COLUMN_ENABLEFILELOGGING = "enableFileLogging";
    static final String COLUMN_SHOWCOLUMNWIDTH = "showColumnWidth";
    static final String COLUMN_REMAININGCOLUMNWIDTH = "remainingColumnWidth";
    static final String COLUMN_SEASONCOLUMNWIDTH = "seasonColumnWidth";
    static final String COLUMN_EPISODECOLUMNWIDTH = "episodeColumnWidth";
    static final String COLUMN_SHOWCOLUMNVISIBILITY = "showColumnVisibility";
    static final String COLUMN_REMAININGCOLUMNVISIBILITY = "remainingColumnVisibility";
    static final String COLUMN_SEASONCOLUMNVISIBILITY = "seasonColumnVisibility";
    static final String COLUMN_EPISODECOLUMNVISIBILITY = "episodeColumnVisibility";
    static final String COLUMN_VIDEOPLAYERTYPE = "videoPlayer";
    static final String COLUMN_VIDEOPLAYERLOCATION = "videoPlayerLocation";
    // -- User Show Settings -- \\
    static final String COLUMN_CURRENTSEASON = "currentSeason";
    static final String COLUMN_CURRENTEPISODE = "currentEpisode";
    static final String COLUMN_ACTIVE = "active";
    static final String COLUMN_IGNORED = "ignored";
    static final String COLUMN_HIDDEN = "hidden";
    static final String COLUMN_USERALTERED = "userAltered";
    // -- User Episode Settings -- \\
    static final String COLUMN_EPISODETIMEPOSITION = "episodeTimePosition";
    // -- Show -- \\
    static final String COLUMN_SHOWNAME = "showName";
    static final String COLUMN_SEASON = "season";
    static final String COLUMN_EPISODE = "episode";
    static final String COLUMN_FILE = "file";
    static final String COLUMN_PARTOFDOUBLEEPISODE = "partOfDoubleEpisode";
    static final String COLUMN_SHOWEXISTS = "showExists";
    // -- Directory -- \\
    static final String COLUMN_DIRECTORY = "directory";
    static final String COLUMN_DIRECTORYACTIVE = "active";
    // -- Program Settings -- \\
    static final String COLUMN_DEFAULTUSER = "defaultUser";
    // -- Show Changes -- \\
    static final String COLUMN_CHANGE_ID = "changeID";
    static final String COLUMN_SHOWFOUND = "showFound";
    static final String COLUMN_TIMEADDED = "timeAdded";
    static final String COLUMN_USERSEEN = "userSeen";
    // Table Create Strings
    static final String CREATE_USERCHANGETRACKINGTABLE = DBStrings.TABLE_USERCHANGETRACKING + "(" + DBStrings.COLUMN_USER_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_CHANGE_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_USERSEEN + " BOOLEAN NOT NULL)";
    static final String CREATE_SHOWCHANGESTABLE = DBStrings.TABLE_SHOWCHANGES + "(" + DBStrings.COLUMN_CHANGE_ID + " INTEGER UNIQUE NOT NULL, " + DBStrings.COLUMN_SHOW_ID + " INTEGER NOT NULL , " + DBStrings.COLUMN_SEASON + " INTEGER NOT NULL, " + DBStrings.COLUMN_EPISODE + " INTEGER NOT NULL, " + DBStrings.COLUMN_SHOWFOUND + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_TIMEADDED + " INTEGER NOT NULL)";
    static final String CREATE_USERSETTINGSTABLE = DBStrings.TABLE_USERSETTINGS + "(" + DBStrings.COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, " + DBStrings.COLUMN_SHOWUSERNAME + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_UPDATESPEED + " INTEGER NOT NULL," +
            DBStrings.COLUMN_AUTOMATICSHOWUPDATING + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_TIMETOWAITFORDIRECTORY + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_SHOW0REMAINING + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_SHOWACTIVESHOWS + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_LANGUAGE + " VARCHAR(20) NOT NULL, " +
            DBStrings.COLUMN_RECORDCHANGESFORNONACTIVESHOWS + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT + " BOOLEAN NOT NULL, " +
            DBStrings.COLUMN_MOVESTAGEWITHPARENT + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_HAVESTAGEBLOCKPARENTSTAGE + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_ENABLESPECIALEFFECTS + " BOOLEAN NOT NULL, " +
            DBStrings.COLUMN_ENABLEFILELOGGING + " BOOLEAN NOT NULL, " +
            DBStrings.COLUMN_SHOWCOLUMNWIDTH + " FLOAT NOT NULL, " + DBStrings.COLUMN_REMAININGCOLUMNWIDTH + " FLOAT NOT NULL, " +
            DBStrings.COLUMN_SEASONCOLUMNWIDTH + " FLOAT NOT NULL, " + DBStrings.COLUMN_EPISODECOLUMNWIDTH + " FLOAT NOT NULL, " + DBStrings.COLUMN_SHOWCOLUMNVISIBILITY + " BOOLEAN NOT NULL, " +
            DBStrings.COLUMN_REMAININGCOLUMNVISIBILITY + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_SEASONCOLUMNVISIBILITY + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_EPISODECOLUMNVISIBILITY + " BOOLEAN NOT NULL, " +
            DBStrings.COLUMN_VIDEOPLAYERTYPE + " INTEGER NOT NULL, " + DBStrings.COLUMN_VIDEOPLAYERLOCATION + " VARCHAR(" + DBStrings.directoryLength + ") NOT NULL)";
    static final String CREATE_USERSHOWSETTINGSTABLE = DBStrings.TABLE_USERSHOWSETTINGS + "(" + DBStrings.COLUMN_USER_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_SHOW_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_CURRENTSEASON + " INTEGER NOT NULL, " + DBStrings.COLUMN_CURRENTEPISODE + " INTEGER NOT NULL, " + DBStrings.COLUMN_ACTIVE + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_IGNORED + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_HIDDEN + " BOOLEAN NOT NULL, " + COLUMN_USERALTERED + "BOOLEAN NOT NULL)";
    static final String CREATE_USEREPISODESETTINGSTABLE = DBStrings.TABLE_USEREPISODESETTINGS + "(" + DBStrings.COLUMN_USER_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_EPISODE_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_EPISODETIMEPOSITION + " INTEGER NOT NULL)";
    static final String CREATE_USERSTABLE = DBStrings.TABLE_USERS + "(" + DBStrings.COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, " + DBStrings.COLUMN_USERNAME + " VARCHAR(20) NOT NULL," + DBStrings.COLUMN_SHOWUSERNAME + " BOOLEAN NOT NULL )";
    static final String CREATE_DIRECTORIESTABLE = DBStrings.TABLE_DIRECTORIES + "(" + DBStrings.COLUMN_DIRECTORY_ID + " INTEGER NOT NULL UNIQUE, " + DBStrings.COLUMN_DIRECTORY + " VARCHAR(" + DBStrings.directoryLength + ") NOT NULL UNIQUE, " + DBStrings.COLUMN_DIRECTORYPRIORITY + " INTEGER NOT NULL, " + DBStrings.COLUMN_DIRECTORYACTIVE + " BOOLEAN NOT NULL)";
    static final String CREATE_SHOWSTABLE = DBStrings.TABLE_SHOWS + "(" + DBStrings.COLUMN_SHOW_ID + " INTEGER UNIQUE NOT NULL, " + DBStrings.COLUMN_SHOWNAME + " VARCHAR(80) UNIQUE NOT NULL, " + DBStrings.COLUMN_SHOWEXISTS + " BOOLEAN NOT NULL)";
    static final String CREATE_SEASONSTABLE = DBStrings.TABLE_SEASONS + "(" + DBStrings.COLUMN_SHOW_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_SEASON + " INTEGER NOT NULL)";
    static final String CREATE_EPISODESTABLE = DBStrings.TABLE_EPISODES + "(" + DBStrings.COLUMN_SHOW_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_SEASON + " INTEGER NOT NULL, " + DBStrings.COLUMN_EPISODE + " INTEGER NOT NULL, " + DBStrings.COLUMN_PARTOFDOUBLEEPISODE + " BOOLEAN NOT NULL, " + DBStrings.COLUMN_EPISODE_ID + " INTEGER NOT NULL)";
    static final String CREATE_EPISODEFILESTABLE = DBStrings.TABLE_EPISODEFILES + "(" + DBStrings.COLUMN_EPISODE_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_DIRECTORY_ID + " INTEGER NOT NULL, " + DBStrings.COLUMN_FILE + " VARCHAR(200) NOT NULL)";
    static final String DBChangeTracker_addChangeSQL = "INSERT INTO " + DBStrings.TABLE_SHOWCHANGES + " VALUES(?,?,?,?,?,?)";
    static final String DBChangeTracker_getChangeInfoSQL = "SELECT * FROM " + DBStrings.TABLE_SHOWCHANGES + " WHERE " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_removeChangeSQL = "DELETE FROM " + DBStrings.TABLE_SHOWCHANGES + " WHERE " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_addUserChangeSQL = "INSERT INTO " + DBStrings.TABLE_USERCHANGETRACKING + " VALUES(?,?,?)";
    static final String DBChangeTracker_getUserChangesSQL = "SELECT " + DBStrings.COLUMN_CHANGE_ID + " FROM " + DBStrings.TABLE_USERCHANGETRACKING + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBChangeTracker_removeUserChangeSQL = "DELETE FROM " + DBStrings.TABLE_USERCHANGETRACKING + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_removeChangeForUsersSQL = "DELETE FROM " + DBStrings.TABLE_USERCHANGETRACKING + " WHERE " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_setUserChangeSeenSQL = "UPDATE " + DBStrings.TABLE_USERCHANGETRACKING + " SET " + DBStrings.COLUMN_USERSEEN + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_findUsersSeenChangeSQL = "SELECT " + DBStrings.COLUMN_USER_ID + " FROM " + DBStrings.TABLE_USERCHANGETRACKING + " WHERE " + DBStrings.COLUMN_USERSEEN + "=" + Boolean.TRUE + " AND " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_changeFoundStatusSQL = "UPDATE " + DBStrings.TABLE_SHOWCHANGES + " SET " + DBStrings.COLUMN_SHOWFOUND + "=? WHERE " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_getUsersWithChangeSQL = "SELECT " + DBStrings.COLUMN_USER_ID + " FROM " + DBStrings.TABLE_USERCHANGETRACKING + " WHERE " + DBStrings.COLUMN_CHANGE_ID + "=?";
    static final String DBChangeTracker_getChangesWithUsersSQL = "SELECT " + DBStrings.COLUMN_CHANGE_ID + " FROM " + DBStrings.TABLE_USERCHANGETRACKING;
    //getAllChangesSQL = "SELECT " + DBStrings.COLUMN_CHANGE_ID + " FROM " + DBStrings.TABLE_SHOWCHANGES);
    static final String DBChangeTracker_deleteAllChangesForUserSQL = "DELETE FROM " + DBStrings.TABLE_USERCHANGETRACKING + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBChangeTracker_setAllSeenForUserSQL = "UPDATE " + DBStrings.TABLE_USERCHANGETRACKING + " SET " + DBStrings.COLUMN_USERSEEN + "=" + Boolean.TRUE + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBDirectoryHandler_getDirectoryIDSQL = "SELECT " + DBStrings.COLUMN_DIRECTORY_ID + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORY + "=?";
    static final String DBDirectoryHandler_getDirectorySQL = "SELECT " + DBStrings.COLUMN_DIRECTORY + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBDirectoryHandler_addDirectorySQL = "INSERT INTO " + DBStrings.TABLE_DIRECTORIES + " VALUES (?, ?, ?, ?)";
    static final String DBDirectoryHandler_changeDirectorySQL = "UPDATE " + DBStrings.TABLE_DIRECTORIES + " SET " + DBStrings.COLUMN_DIRECTORY + "=? WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    //removeDirectorySQL = "DELETE FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?");
    static final String DBDirectoryHandler_checkDirectorySQL = "SELECT " + DBStrings.COLUMN_DIRECTORY + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORY + "=?";
    static final String DBDirectoryHandler_getDirectoryPrioritySQL = "SELECT " + DBStrings.COLUMN_DIRECTORYPRIORITY + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBDirectoryHandler_updateDirectoryPrioritySQL = "UPDATE " + DBStrings.TABLE_DIRECTORIES + " SET " + DBStrings.COLUMN_DIRECTORYPRIORITY + "=?" + " WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBDirectoryHandler_updateDirectoryPriorityWithoutIDSQL = "UPDATE " + DBStrings.TABLE_DIRECTORIES + " SET " + DBStrings.COLUMN_DIRECTORYPRIORITY + "=? WHERE " + DBStrings.COLUMN_DIRECTORYPRIORITY + "=?";
    static final String DBDirectoryHandler_doesContainPrioritySQL = "SELECT " + DBStrings.COLUMN_DIRECTORYPRIORITY + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORYPRIORITY + "=?";
    static final String DBDirectoryHandler_getDirectoryActiveStatusSQL = "SELECT " + DBStrings.COLUMN_DIRECTORYACTIVE + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBDirectoryHandler_setDirectoryActiveStatusSQL = "UPDATE " + DBStrings.TABLE_DIRECTORIES + " SET " + DBStrings.COLUMN_DIRECTORYACTIVE + "=? WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBDirectoryHandler_getAllDirectoriesSQL = "SELECT " + DBStrings.COLUMN_DIRECTORY_ID + " FROM " + DBStrings.TABLE_DIRECTORIES;
    static final String DBDirectoryHandler_getDirectoriesFromActiveStatusSQL = "SELECT " + DBStrings.COLUMN_DIRECTORY_ID + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORYACTIVE + "=?";
    static final String DBDirectoryHandler_getDirectoryFromPrioritySQL = "SELECT " + DBStrings.COLUMN_DIRECTORY_ID + " FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + DBStrings.COLUMN_DIRECTORYPRIORITY + "=?";
    static final String DBProgramSettingsManager_getDefaultUserSQL = "SELECT " + DBStrings.COLUMN_DEFAULTUSER + " FROM " + DBStrings.TABLE_PROGRAMSETTINGS;
    static final String DBProgramSettingsManager_changeDefaultUserSQL = "UPDATE " + DBStrings.TABLE_PROGRAMSETTINGS + " SET " + DBStrings.COLUMN_DEFAULTUSER + "=?";
    static final String DBShowManager_getAllShowsSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_SHOWS;
    static final String DBShowManager_addShowSQL = "INSERT INTO " + DBStrings.TABLE_SHOWS + " VALUES (?, ?, ?)";
    //removeShowSQL = "DELETE FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?");
    static final String DBShowManager_addSeasonSQL = "INSERT INTO " + DBStrings.TABLE_SEASONS + " VALUES (?, ?)";
    static final String DBShowManager_removeSeasonsSQL = "DELETE FROM " + DBStrings.TABLE_SEASONS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_addEpisodeSQL = "INSERT INTO " + DBStrings.TABLE_EPISODES + " VALUES (?, ?, ?, ?, ?)";
    static final String DBShowManager_removeEpisodesSQL = "DELETE FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_removeEpisodeFilesSQL = "DELETE FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_checkShowIDSQL = "SELECT " + DBStrings.COLUMN_SHOWNAME + " FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_getShowIDSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOWNAME + " =?";
    static final String DBShowManager_checkForShowSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOWNAME + "=?";
    static final String DBShowManager_getSeasonsSQL = "SELECT * FROM " + DBStrings.TABLE_SEASONS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_getSeasonEpisodesSQL = "SELECT " + DBStrings.COLUMN_EPISODE + " FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=? AND " + DBStrings.COLUMN_SEASON + "=?";
    static final String DBShowManager_getEpisodeSQL = "SELECT " + DBStrings.COLUMN_FILE + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=? AND " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBShowManager_isEpisodePartOfDoubleEpisodeSQL = "SELECT " + DBStrings.COLUMN_PARTOFDOUBLEEPISODE + " FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    //        updateEpisodeFileSQL = "UPDATE " + DBStrings.episodes + " SET " + DBStrings.file + "=? WHERE " + DBStrings.showID + "=? AND " + DBStrings.season + "=? AND " + DBStrings.episode + "=?");
    static final String DBShowManager_addEpisodeFileSQL = "INSERT INTO " + DBStrings.TABLE_EPISODEFILES + " VALUES (?, ?, ?)";
    static final String DBShowManager_doesContainEpisodeSQL = "SELECT " + DBStrings.COLUMN_EPISODE_ID + " FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_removeEpisodeFileSQL = "DELETE FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_FILE + "=?";
    static final String DBShowManager_getShowNameSQL = "SELECT " + DBStrings.COLUMN_SHOWNAME + " FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_getShowIDFromEpisodeIDSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_getEpisodeDirectoriesSQL = "SELECT " + DBStrings.COLUMN_DIRECTORY_ID + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_checkForSeasonSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_SEASONS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=? AND " + DBStrings.COLUMN_SEASON + "=?";
    static final String DBShowManager_getEpisodeSeasonSQL = "SELECT " + DBStrings.COLUMN_SEASON + " FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_getShowEpisodeFilesSQL = "SELECT " + DBStrings.COLUMN_FILE + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_setShowExistsStatusSQL = "UPDATE " + DBStrings.TABLE_SHOWS + " SET " + DBStrings.COLUMN_SHOWEXISTS + "=? WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_getShowExistsStatusSQL = "SELECT " + DBStrings.COLUMN_SHOWEXISTS + " FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_getEpisodeFileDirectoriesSQL = "SELECT " + DBStrings.COLUMN_DIRECTORY_ID + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBShowManager_getAllShowsThatExistSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_SHOWS + " WHERE " + DBStrings.COLUMN_SHOWEXISTS + "=" + Boolean.TRUE;
    static final String DBShowManager_getAllEpisodeIDsForDirectorySQL = "SELECT " + DBStrings.COLUMN_EPISODE_ID + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBShowManager_getEpisodeFilesForDirectorySQL = "SELECT " + DBStrings.COLUMN_FILE + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=? AND " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBShowManager_verifyEpisodeIDIsInDirectorySQL = "SELECT " + DBStrings.COLUMN_EPISODE_ID + " FROM " + DBStrings.TABLE_EPISODEFILES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=? AND " + DBStrings.COLUMN_DIRECTORY_ID + "=?";
    static final String DBShowManager_getAllShowEpisodeIDsSQL = "SELECT " + DBStrings.COLUMN_EPISODE_ID + " FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBShowManager_removeSeasonSQL = "DELETE FROM " + DBStrings.TABLE_SEASONS + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=? AND " + DBStrings.COLUMN_SEASON + "=?";
    static final String DBShowManager_removeSeasonEpisodesSQL = "DELETE FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_SHOW_ID + "=? AND " + DBStrings.COLUMN_SEASON + "=?";
    static final String DBShowManager_removeEpisodeSQL = "DELETE FROM " + DBStrings.TABLE_EPISODES + " WHERE " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBUserManager_insertUserSQL = "INSERT INTO " + DBStrings.TABLE_USERS + " VALUES (?, ?, ?)";
    static final String DBUserManager_changeUsernameSQL = "UPDATE " + DBStrings.TABLE_USERS + " SET " + DBStrings.COLUMN_USERNAME + "=? WHERE " + DBStrings.COLUMN_USERNAME + " =?";
    static final String DBUserManager_getShowUsernameSQL = "SELECT " + DBStrings.COLUMN_SHOWUSERNAME + " FROM " + DBStrings.TABLE_USERS + " WHERE " + DBStrings.COLUMN_USERNAME + "=?";
    static final String DBUserManager_checkUserIDSQL = "SELECT " + DBStrings.COLUMN_USERNAME + " FROM " + DBStrings.TABLE_USERS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserManager_getUserIDSQL = "SELECT " + DBStrings.COLUMN_USER_ID + " FROM " + DBStrings.TABLE_USERS + " WHERE " + DBStrings.COLUMN_USERNAME + " =?";
    static final String DBUserManager_getUsernameSQL = "SELECT " + DBStrings.COLUMN_USERNAME + " FROM " + DBStrings.TABLE_USERS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserManager_getAllUsersSQL = "SELECT " + DBStrings.COLUMN_USER_ID + " FROM " + DBStrings.TABLE_USERS;
    static final String DBUserSettingsManager_addShowSettingsSQL = "INSERT INTO " + DBStrings.TABLE_USERSHOWSETTINGS + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    static final String DBUserSettingsManager_addEpisodeSettingsSQL = "INSERT INTO " + DBStrings.TABLE_USEREPISODESETTINGS + " VALUES (?, ?, ?)";
    static final String DBUserSettingsManager_insertSettingsSQL = "INSERT INTO " + DBStrings.TABLE_USERSETTINGS + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static final String DBUserSettingsManager_getEpisodePositionSQL = "SELECT " + DBStrings.COLUMN_EPISODETIMEPOSITION + " FROM " + DBStrings.TABLE_USEREPISODESETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBUserSettingsManager_setEpisodePositionSQL = "UPDATE " + DBStrings.TABLE_USEREPISODESETTINGS + " SET " + DBStrings.COLUMN_EPISODETIMEPOSITION + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBUserSettingsManager_removeEpisodeSQL = "DELETE FROM " + DBStrings.TABLE_USEREPISODESETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_EPISODE_ID + "=?";
    static final String DBUserSettingsManager_getShowsForUserSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserShowSeasonSQL = "SELECT " + DBStrings.COLUMN_CURRENTSEASON + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserShowEpisodeSQL = "SELECT " + DBStrings.COLUMN_CURRENTEPISODE + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserLanguageSQL = "SELECT " + DBStrings.COLUMN_LANGUAGE + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserLanguageSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_LANGUAGE + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserDoShowUpdatingSQL = "SELECT " + DBStrings.COLUMN_AUTOMATICSHOWUPDATING + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserDoShowUpdatingSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_AUTOMATICSHOWUPDATING + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserUpdateSpeedSQL = "SELECT " + DBStrings.COLUMN_UPDATESPEED + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserUpdateSpeedSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_UPDATESPEED + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserTimeToWaitForDirectorySQL = "SELECT " + DBStrings.COLUMN_TIMETOWAITFORDIRECTORY + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserTimeToWaitForDirectorySQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_TIMETOWAITFORDIRECTORY + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserDoFileLoggingSQL = "SELECT " + DBStrings.COLUMN_ENABLEFILELOGGING + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserDoFileLoggingSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_ENABLEFILELOGGING + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserDoSpecialEffectsSQL = "SELECT " + DBStrings.COLUMN_ENABLESPECIALEFFECTS + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserDoSpecialEffectsSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_ENABLESPECIALEFFECTS + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserShow0RemainingSQL = "SELECT " + DBStrings.COLUMN_SHOW0REMAINING + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserShow0RemainingSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SHOW0REMAINING + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserShowActiveShowsSQL = "SELECT " + DBStrings.COLUMN_SHOWACTIVESHOWS + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserShowActiveShowsSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SHOWACTIVESHOWS + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserRecordChangesForNonActiveShowsSQL = "SELECT " + DBStrings.COLUMN_RECORDCHANGESFORNONACTIVESHOWS + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserRecordChangesForNonActiveShowsSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_RECORDCHANGESFORNONACTIVESHOWS + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserRecordChangesSeasonsLowerThanCurrentSQL = "SELECT " + DBStrings.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserRecordChangesSeasonsLowerThanCurrentSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserMoveStageWithParentSQL = "SELECT " + DBStrings.COLUMN_MOVESTAGEWITHPARENT + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserMoveStageWithParentSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_MOVESTAGEWITHPARENT + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserHaveStageBlockParentStageSQL = "SELECT " + DBStrings.COLUMN_HAVESTAGEBLOCKPARENTSTAGE + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserHaveStageBlockParentStageSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_HAVESTAGEBLOCKPARENTSTAGE + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserEnableFileLoggingSQL = "SELECT " + DBStrings.COLUMN_ENABLEFILELOGGING + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserVideoPlayerTypeSQL = "SELECT " + DBStrings.COLUMN_VIDEOPLAYERTYPE + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserVideoPlayerTypeSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_VIDEOPLAYERTYPE + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserVideoPlayerLocationSQL = "SELECT " + DBStrings.COLUMN_VIDEOPLAYERLOCATION + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserVideoPlayerLocationSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_VIDEOPLAYERLOCATION + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserShowSeasonSQL = "UPDATE " + DBStrings.TABLE_USERSHOWSETTINGS + " SET " + DBStrings.COLUMN_CURRENTSEASON + "=?" + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_setUserShowEpisodeSQL = "UPDATE " + DBStrings.TABLE_USERSHOWSETTINGS + " SET " + DBStrings.COLUMN_CURRENTEPISODE + "=?" + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserShowIgnoredStatusSQL = "SELECT " + DBStrings.COLUMN_IGNORED + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_setUserShowIgnoredStatusSQL = "UPDATE " + DBStrings.TABLE_USERSHOWSETTINGS + " SET " + DBStrings.COLUMN_IGNORED + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserShowActiveStatusSQL = "SELECT " + DBStrings.COLUMN_ACTIVE + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_setUserShowActiveStatusSQL = "UPDATE " + DBStrings.TABLE_USERSHOWSETTINGS + " SET " + DBStrings.COLUMN_ACTIVE + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserShowHiddenStatusSQL = "SELECT " + DBStrings.COLUMN_HIDDEN + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_setUserShowHiddenStatusSQL = "UPDATE " + DBStrings.TABLE_USERSHOWSETTINGS + " SET " + DBStrings.COLUMN_HIDDEN + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_checkIfShowSettingsExistForUserSQL = "SELECT  " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserShowUsernameSQL = "SELECT " + DBStrings.COLUMN_SHOWUSERNAME + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserShowUsernameSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SHOWUSERNAME + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserShowColumnVisibilitySQL = "SELECT " + DBStrings.COLUMN_SHOWCOLUMNVISIBILITY + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserShowColumnVisibilitySQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SHOWCOLUMNVISIBILITY + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserShowColumnWidthSQL = "SELECT " + DBStrings.COLUMN_SHOWCOLUMNWIDTH + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserShowColumnWidthSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SHOWCOLUMNWIDTH + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserSeasonColumnVisibilitySQL = "SELECT " + DBStrings.COLUMN_SEASONCOLUMNVISIBILITY + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserSeasonColumnVisibilitySQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SEASONCOLUMNVISIBILITY + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserSeasonColumnWidthSQL = "SELECT " + DBStrings.COLUMN_SEASONCOLUMNWIDTH + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserSeasonColumnWidthSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_SEASONCOLUMNWIDTH + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserEpisodeColumnVisibilitySQL = "SELECT " + DBStrings.COLUMN_EPISODECOLUMNVISIBILITY + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserEpisodeColumnVisibilitySQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_EPISODECOLUMNVISIBILITY + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserEpisodeColumnWidthSQL = "SELECT " + DBStrings.COLUMN_EPISODECOLUMNWIDTH + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserEpisodeColumnWidthSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_EPISODECOLUMNWIDTH + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserRemainingColumnVisibilitySQL = "SELECT " + DBStrings.COLUMN_REMAININGCOLUMNVISIBILITY + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserRemainingColumnVisibilitySQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_REMAININGCOLUMNVISIBILITY + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_getUserRemainingColumnWidthSQL = "SELECT " + DBStrings.COLUMN_REMAININGCOLUMNWIDTH + " FROM " + DBStrings.TABLE_USERSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_setUserRemainingColumnWidthSQL = "UPDATE " + DBStrings.TABLE_USERSETTINGS + " SET " + DBStrings.COLUMN_REMAININGCOLUMNWIDTH + "=? WHERE " + DBStrings.COLUMN_USER_ID + "=?";
    static final String DBUserSettingsManager_checkUserHasShowIDSQL = "SELECT " + DBStrings.COLUMN_SHOW_ID + " FROM " + DBStrings.TABLE_USERSHOWSETTINGS + " WHERE " + DBStrings.COLUMN_USER_ID + "=? AND " + DBStrings.COLUMN_SHOW_ID + "=?";
    static final String DBUserSettingsManager_getUserIgnoredShows = "SELECT " + COLUMN_SHOW_ID + " FROM " + TABLE_USERSHOWSETTINGS + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_IGNORED + "=TRUE";
    static final String DBUserSettingsManager_getUserHiddenShows = "SELECT " + COLUMN_SHOW_ID + " FROM " + TABLE_USERSHOWSETTINGS + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_HIDDEN + "=TRUE";
    static final String DBUserSettingsManager_getUserActiveShows = "SELECT " + COLUMN_SHOW_ID + " FROM " + TABLE_USERSHOWSETTINGS + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_ACTIVE + "=TRUE";
    static final String DBUserSettingsManager_getUserNonIgnoredShows = "SELECT " + COLUMN_SHOW_ID + " FROM " + TABLE_USERSHOWSETTINGS + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_IGNORED + "=FALSE";
    static final String DBUserSettingsManager_getUserInactiveShows = "SELECT " + COLUMN_SHOW_ID + " FROM " + TABLE_USERSHOWSETTINGS + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_ACTIVE + "=FALSE";
    private static final String TABLEVERSION = "TableVersion";
    static final String COLUMN_PROGRAMSETTINGSTABLEVERSION = TABLE_PROGRAMSETTINGS + TABLEVERSION;
    static final String COLUMN_USERSETTINGSTABLEVERSION = TABLE_USERSETTINGS + TABLEVERSION;
    static final String COLUMN_SHOWSTABLEVERSION = TABLE_SHOWS + TABLEVERSION;
    static final String COLUMN_SEASONSTABLEVERSION = TABLE_SEASONS + TABLEVERSION;
    static final String COLUMN_EPISODESTABLEVERSION = TABLE_EPISODES + TABLEVERSION;
    static final String COLUMN_EPISODEFILESTABLEVERSION = TABLE_EPISODEFILES + TABLEVERSION;
    static final String COLUMN_DIRECTORIESTABLEVERSION = TABLE_DIRECTORIES + TABLEVERSION;
    static final String COLUMN_USERSTABLEVERSION = TABLE_USERS + TABLEVERSION;
    static final String COLUMN_USERSHOWSETTINGSTABLEVERSION = TABLE_USERSHOWSETTINGS + TABLEVERSION;
    static final String COLUMN_USEREPISODESETTINGSTABLEVERSION = TABLE_USEREPISODESETTINGS + TABLEVERSION;
    static final String COLUMN_SHOWCHANGESTABLEVERSION = TABLE_SHOWCHANGES + TABLEVERSION;
    static final String COLUMN_USERCHANGETRACKINGTABLEVERSION = TABLE_USERCHANGETRACKING + TABLEVERSION;
    static final String CREATE_PROGRAMSETTINGSTABLE = DBStrings.TABLE_PROGRAMSETTINGS + "(" +
            DBStrings.COLUMN_PROGRAMSETTINGSTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_USERSETTINGSTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_SHOWSTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_SEASONSTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_EPISODESTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_EPISODEFILESTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_DIRECTORIESTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_USERSTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_SHOWCHANGESTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_USERCHANGETRACKINGTABLEVERSION + " INTEGER NOT NULL, " +
            DBStrings.COLUMN_DEFAULTUSER + " INTEGER NOT NULL " + ")";
}
