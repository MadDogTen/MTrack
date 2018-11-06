package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class DBShowManager {
    private final Logger log = Logger.getLogger(DBShowManager.class.getName());
    private final DBManager dbManager;

    private PreparedStatement getAllShows;
    private PreparedStatement addShow;
    //private PreparedStatement removeShow;
    private PreparedStatement addSeason;
    private PreparedStatement removeSeasons;
    private PreparedStatement addEpisode;
    private PreparedStatement removeEpisodes;
    private PreparedStatement removeEpisodeFiles;
    private PreparedStatement checkShowID;
    private PreparedStatement getShowID;
    private PreparedStatement checkForShow;
    private PreparedStatement getSeasons;
    private PreparedStatement getSeasonEpisodes;
    private PreparedStatement getEpisode;
    private PreparedStatement isEpisodePartOfDoubleEpisode;
    // private PreparedStatement updateEpisodeFile;
    private PreparedStatement addEpisodeFile;
    private PreparedStatement doesContainEpisode;
    private PreparedStatement removeEpisodeFile;
    private PreparedStatement getShowName;
    private PreparedStatement getShowIDFromEpisodeID;
    private PreparedStatement getEpisodeDirectories;
    private PreparedStatement checkForSeason;
    private PreparedStatement getEpisodeSeason;
    private PreparedStatement getShowEpisodeFiles;
    private PreparedStatement setShowExistsStatus;
    private PreparedStatement getShowExistsStatus;
    private PreparedStatement getEpisodeFileDirectories;
    private PreparedStatement getAllShowsThatExist;
    private PreparedStatement getAllEpisodeIDsForDirectory;
    private PreparedStatement getEpisodeFilesForDirectory;
    private PreparedStatement verifyEpisodeIDIsInDirectory;
    private PreparedStatement getAllShowEpisodeIDs;
    private PreparedStatement removeSeason;
    private PreparedStatement removeSeasonEpisodes;
    private PreparedStatement removeEpisode;

    public DBShowManager(DBManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.createTable(DBStrings.CREATE_SHOWSTABLE); // TODO Add option to change the show name displayed
        this.dbManager.createTable(DBStrings.CREATE_SEASONSTABLE);
        this.dbManager.createTable(DBStrings.CREATE_EPISODESTABLE);
        this.dbManager.createTable(DBStrings.CREATE_EPISODEFILESTABLE);
    }

    public synchronized ArrayList<Integer> getAllShows() {
        if (isNull(getAllShows)) getAllShows = dbManager.prepareStatement(DBStrings.DBShowManager_getAllShowsSQL);
        ArrayList<Integer> allShows = new ArrayList<>();
        try {
            try (ResultSet resultSet = getAllShows.executeQuery()) {
                while (resultSet.next()) allShows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return allShows;
    }

    public synchronized ArrayList<Integer> getAllShowsThatExist() {
        if (isNull(getAllShowsThatExist))
            getAllShowsThatExist = dbManager.prepareStatement(DBStrings.DBShowManager_getAllShowsThatExistSQL);
        ArrayList<Integer> shows = new ArrayList<>();
        try {
            try (ResultSet resultSet = getAllShowsThatExist.executeQuery()) {
                while (resultSet.next()) shows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return shows;
    }

    public synchronized String getShowName(int showID) {
        if (isNull(getShowName)) getShowName = dbManager.prepareStatement(DBStrings.DBShowManager_getShowNameSQL);
        String showName = Strings.EmptyString;
        try {
            getShowName.setInt(1, showID);
            try (ResultSet resultSet = getShowName.executeQuery()) {
                if (resultSet.next()) showName = resultSet.getString(DBStrings.COLUMN_SHOWNAME);
            }
            getShowName.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showName;
    }

    public synchronized int getShowIDFromEpisodeID(int episodeID) {
        if (isNull(getShowIDFromEpisodeID))
            getShowIDFromEpisodeID = dbManager.prepareStatement(DBStrings.DBShowManager_getShowIDFromEpisodeIDSQL);
        int showID = -2;
        try {
            getShowIDFromEpisodeID.setInt(1, episodeID);
            try (ResultSet resultSet = getShowIDFromEpisodeID.executeQuery()) {
                if (resultSet.next()) showID = resultSet.getInt(DBStrings.COLUMN_SHOW_ID);
            }
            getShowIDFromEpisodeID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showID;
    }

    public synchronized String getShowNameFromEpisodeID(int episodeID) {
        return getShowName(getShowIDFromEpisodeID(episodeID));
    }

    public synchronized int getShowID(String showName) throws SQLException {
        if (isNull(getShowID)) getShowID = dbManager.prepareStatement(DBStrings.DBShowManager_getShowIDSQL);
        getShowID.setString(1, showName);
        int result = -2;
        try (ResultSet resultSet = getShowID.executeQuery()) {
            if (resultSet.next()) result = resultSet.getInt(DBStrings.COLUMN_SHOW_ID);
            else log.warning("Couldn't find ShowID for \"" + showName + "\".");
        }
        getShowID.clearParameters();
        return result;
    }

    public synchronized boolean isEpisodePartOfDoubleEpisode(int episodeID) {
        if (isNull(isEpisodePartOfDoubleEpisode))
            isEpisodePartOfDoubleEpisode = dbManager.prepareStatement(DBStrings.DBShowManager_isEpisodePartOfDoubleEpisodeSQL);
        boolean result = false;
        try {
            isEpisodePartOfDoubleEpisode.setInt(1, episodeID);
            try (ResultSet resultSet = isEpisodePartOfDoubleEpisode.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_PARTOFDOUBLEEPISODE);
            }
            isEpisodePartOfDoubleEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private synchronized int generateShowID() throws SQLException {
        if (isNull(checkShowID)) checkShowID = dbManager.prepareStatement(DBStrings.DBShowManager_checkShowIDSQL);
        Random random = new Random();
        int showID;
        ResultSet resultSet;
        do {
            showID = random.nextInt(9999) + 1000;
            checkShowID.setInt(1, showID);
            resultSet = checkShowID.executeQuery();
        } while (resultSet.next());
        resultSet.close();
        checkShowID.clearParameters();
        return showID;
    }

    public synchronized int[] addShow(String show) {
        if (isNull(addShow)) addShow = dbManager.prepareStatement(DBStrings.DBShowManager_addShowSQL);
        int showID = doesAlreadyContainShow(show);
        if (showID != -2) {
            if (!doesShowExist(showID)) {
                setShowExists(showID, true);
                return new int[]{showID, 2};
            }
            return new int[]{showID, 0};
        } else try {
            showID = generateShowID();
            addShow.setInt(1, showID);
            addShow.setString(2, show);
            addShow.setBoolean(3, true);
            addShow.execute();
            addShow.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return new int[]{showID, 1};
    }

    public synchronized void removeShow(int showID) { // TODO Add checking for if any users modified the show setting, and if not, completely remove the show.
        if (isNull(removeSeasons)) removeSeasons = dbManager.prepareStatement(DBStrings.DBShowManager_removeSeasonsSQL);
        if (isNull(removeEpisodes))
            removeEpisodes = dbManager.prepareStatement(DBStrings.DBShowManager_removeEpisodesSQL);
        try {
            setShowExists(showID, false);
            Set<Integer> seasons = getSeasons(showID);
            removeSeasons.setInt(1, showID);
            removeSeasons.execute();
            removeSeasons.clearParameters();
            seasons.forEach(season -> getSeasonEpisodes(showID, season).forEach(episode -> removeEpisodeFiles(getEpisodeID(showID, season, episode))));
            removeEpisodes.setInt(1, showID);
            removeEpisodes.execute();
            removeEpisodes.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void removeSeason(int showID, int season) {
        if (isNull(removeSeason)) removeSeason = dbManager.prepareStatement(DBStrings.DBShowManager_removeSeasonSQL);
        try {
            getSeasonEpisodes(showID, season).forEach(episode -> removeEpisodeFiles(getEpisodeID(showID, season, episode)));
            removeSeasonEpisodes(showID, season);
            removeSeason.setInt(1, showID);
            removeSeason.setInt(2, season);
            removeSeason.execute();
            removeSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void removeEpisode(int episodeID) {
        if (isNull(removeEpisode)) removeEpisode = dbManager.prepareStatement(DBStrings.DBShowManager_removeEpisodeSQL);
        try {
            removeEpisodeFiles(episodeID);
            removeEpisode.setInt(1, episodeID);
            removeEpisode.execute();
            removeEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void removeDirectory(int directoryID) { // TODO Verify this works correctly
        getAllShowsForDirectory(directoryID).forEach(showID -> {
            getSeasons(showID).forEach(season -> {
                getSeasonEpisodes(showID, season).forEach(episode -> {
                    int episodeID = getEpisodeID(showID, season, episode);
                    getEpisodeFilesForDirectory(episodeID, directoryID).forEach(this::removeEpisodeFile);
                    if (getShowEpisodeFiles(episodeID).isEmpty()) {
                        removeEpisode(episodeID);
                        ClassHandler.changeReporter().addChange(showID, season, episode, false);
                    }
                });
                if (getSeasonEpisodes(showID, season).isEmpty()) {
                    removeSeason(showID, season);
                    ClassHandler.changeReporter().addChange(showID, season, -2, false);
                }
            });
            if (getSeasons(showID).isEmpty()) {
                removeShow(showID);
                ClassHandler.changeReporter().addChange(showID, -2, -2, false);
            }
        });
    }

    public synchronized void removeSeasonEpisodes(int showID, int season) {
        if (isNull(removeSeasonEpisodes))
            removeSeasonEpisodes = dbManager.prepareStatement(DBStrings.DBShowManager_removeSeasonEpisodesSQL);
        try {
            removeSeasonEpisodes.setInt(1, showID);
            removeSeasonEpisodes.setInt(2, season);
            removeSeasonEpisodes.execute();
            removeSeasonEpisodes.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private synchronized void setShowExists(int showID, boolean exists) {
        if (isNull(setShowExistsStatus))
            setShowExistsStatus = dbManager.prepareStatement(DBStrings.DBShowManager_setShowExistsStatusSQL);
        try {
            setShowExistsStatus.setBoolean(1, exists);
            setShowExistsStatus.setInt(2, showID);
            setShowExistsStatus.execute();
            setShowExistsStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean doesShowExist(int showID) {
        if (isNull(getShowExistsStatus))
            getShowExistsStatus = dbManager.prepareStatement(DBStrings.DBShowManager_getShowExistsStatusSQL);
        boolean result = false;
        try {
            getShowExistsStatus.setInt(1, showID);
            try (ResultSet resultSet = getShowExistsStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_SHOWEXISTS);
            }
            getShowExistsStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getSeasons(int showID) {
        if (isNull(getSeasons)) getSeasons = dbManager.prepareStatement(DBStrings.DBShowManager_getSeasonsSQL);
        Set<Integer> seasons = new HashSet<>();
        try {
            getSeasons.setInt(1, showID);
            try (ResultSet resultSet = getSeasons.executeQuery()) {
                while (resultSet.next()) seasons.add(resultSet.getInt(DBStrings.COLUMN_SEASON));
            }
            getSeasons.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return seasons;
    }

    public synchronized Set<Integer> getSeasonEpisodes(int showID, int season) {
        if (isNull(getSeasonEpisodes))
            getSeasonEpisodes = dbManager.prepareStatement(DBStrings.DBShowManager_getSeasonEpisodesSQL);
        Set<Integer> episodes = new HashSet<>();
        try {
            getSeasonEpisodes.setInt(1, showID);
            getSeasonEpisodes.setInt(2, season);
            try (ResultSet resultSet = getSeasonEpisodes.executeQuery()) {
                while (resultSet.next()) episodes.add(resultSet.getInt(DBStrings.COLUMN_EPISODE));
            }
            getSeasonEpisodes.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodes;
    }

    public synchronized void addSeason(int showID, int season) {
        if (isNull(addSeason)) addSeason = dbManager.prepareStatement(DBStrings.DBShowManager_addSeasonSQL);
        if (!getSeasons(showID).contains(season)) {
            try {
                addSeason.setInt(1, showID);
                addSeason.setInt(2, season);
                addSeason.execute();
                addSeason.clearParameters();
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
    }

    public synchronized File getEpisode(int episodeID) {
        if (isNull(getEpisodeDirectories))
            getEpisodeDirectories = dbManager.prepareStatement(DBStrings.DBShowManager_getEpisodeDirectoriesSQL);
        if (isNull(getEpisode)) getEpisode = dbManager.prepareStatement(DBStrings.DBShowManager_getEpisodeSQL);
        File episodeFile = null;
        try {
            getEpisodeDirectories.setInt(1, episodeID);
            Set<Integer> directories = new HashSet<>();
            try (ResultSet resultSet = getEpisodeDirectories.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(DBStrings.COLUMN_DIRECTORY_ID));
            }
            getEpisodeDirectories.clearParameters();
            if (!directories.isEmpty()) {
                String showName = getShowNameFromEpisodeID(episodeID);
                int season = getEpisodeSeason(episodeID);
                if (directories.size() == 1) {
                    getEpisode.setInt(1, episodeID);
                    int directoryID = -2;
                    //noinspection LoopStatementThatDoesntLoop
                    for (int id : directories) {
                        directoryID = id;
                        break;
                    }
                    getEpisode.setInt(2, directoryID);
                    File mainFolder = ClassHandler.directoryController().getDirectoryFromID(directoryID);
                    try (ResultSet resultSet = getEpisode.executeQuery()) {
                        if (resultSet.next())
                            episodeFile = new File(mainFolder + Strings.FileSeparator + showName + Strings.FileSeparator + GenericMethods.getSeasonFolderName(mainFolder, showName, season) + Strings.FileSeparator + resultSet.getString(DBStrings.COLUMN_FILE));
                    }
                    getEpisode.clearParameters();
                } else {
                    getEpisode.setInt(1, episodeID);
                    int directoryID = ClassHandler.directoryController().getDirectoryWithLowestPriority(directories);
                    getEpisode.setInt(2, directoryID);
                    File mainFolder = ClassHandler.directoryController().getDirectoryFromID(directoryID);
                    try (ResultSet resultSet = getEpisode.executeQuery()) {
                        if (resultSet.next())
                            episodeFile = new File(mainFolder + Strings.FileSeparator + showName + Strings.FileSeparator + GenericMethods.getSeasonFolderName(mainFolder, showName, season) + Strings.FileSeparator + resultSet.getString(DBStrings.COLUMN_FILE));
                    }
                    getEpisode.clearParameters();
                }
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return (episodeFile != null && episodeFile.exists()) ? episodeFile : null;
    }

    public synchronized Set<String> getShowEpisodeFiles(int episodeID) {
        if (isNull(getShowEpisodeFiles))
            getShowEpisodeFiles = dbManager.prepareStatement(DBStrings.DBShowManager_getShowEpisodeFilesSQL);
        Set<String> episodeFiles = new HashSet<>();
        try {
            getShowEpisodeFiles.setInt(1, episodeID);
            try (ResultSet resultSet = getShowEpisodeFiles.executeQuery()) {
                while (resultSet.next()) episodeFiles.add(resultSet.getString(DBStrings.COLUMN_FILE));
            }
            getShowEpisodeFiles.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodeFiles;
    }

    private synchronized boolean doesContainEpisode(int episodeID) {
        if (isNull(doesContainEpisode))
            doesContainEpisode = dbManager.prepareStatement(DBStrings.DBShowManager_doesContainEpisodeSQL);
        boolean result = false;
        try {
            doesContainEpisode.setInt(1, episodeID);
            try (ResultSet resultSet = doesContainEpisode.executeQuery()) {
                if (resultSet.next()) result = true;
            }
            doesContainEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }

        return result;
    }

    public synchronized int addEpisode(int showID, int season, int episode, Boolean partOfDoubleEpisode) {
        if (isNull(addEpisode)) addEpisode = dbManager.prepareStatement(DBStrings.DBShowManager_addEpisodeSQL);
        int episodeID = getEpisodeID(showID, season, episode);
        if (episodeID != -2 && !doesContainEpisode(episodeID)) {
            try {
                addEpisode.setInt(1, showID);
                addEpisode.setInt(2, season);
                addEpisode.setInt(3, episode);
                addEpisode.setBoolean(4, partOfDoubleEpisode);
                addEpisode.setInt(5, episodeID);
                addEpisode.execute();
                addEpisode.clearParameters();
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        return episodeID;
    }

    public synchronized void addEpisodeFile(int episodeID, int directoryID, String episodeFile) {
        if (isNull(addEpisodeFile))
            addEpisodeFile = dbManager.prepareStatement(DBStrings.DBShowManager_addEpisodeFileSQL);
        if (episodeID != -2) {
            try {
                addEpisodeFile.setInt(1, episodeID);
                addEpisodeFile.setInt(2, directoryID);
                addEpisodeFile.setString(3, episodeFile);
                addEpisodeFile.execute();
                addEpisodeFile.clearParameters();
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
    }

    private synchronized void removeEpisodeFiles(int episodeID) {
        if (isNull(removeEpisodeFiles))
            removeEpisodeFiles = dbManager.prepareStatement(DBStrings.DBShowManager_removeEpisodeFilesSQL);
        try {
            removeEpisodeFiles.setInt(1, episodeID);
            removeEpisodeFiles.execute();
            removeEpisodeFiles.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void removeEpisodeFile(String episodeFile) {
        if (isNull(removeEpisodeFile))
            removeEpisodeFile = dbManager.prepareStatement(DBStrings.DBShowManager_removeEpisodeFileSQL);
        try {
            removeEpisodeFile.setString(1, episodeFile);
            removeEpisodeFile.execute();
            removeEpisodeFile.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    /*public synchronized void updateEpisodeFile(int showID, int season, int episode, String newFile) {
        try {
            updateEpisodeFile.setString(1, newFile);
            updateEpisodeFile.setInt(2, showID);
            updateEpisodeFile.setInt(3, season);
            updateEpisodeFile.setInt(4, episode);
            updateEpisodeFile.execute();
            updateEpisodeFile.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }*/

    public synchronized boolean doesEpisodeExist(int showID, int season, int episode) {
        return getEpisode(getEpisodeID(showID, season, episode)) != null;
    }

    public synchronized boolean doesEpisodeExist(int episodeID) {
        return getEpisode(episodeID) != null;
    }

    public synchronized boolean doesSeasonExist(int showID, int season) {
        if (isNull(checkForSeason))
            checkForSeason = dbManager.prepareStatement(DBStrings.DBShowManager_checkForSeasonSQL);
        boolean result = false;
        try {
            checkForSeason.setInt(1, showID);
            checkForSeason.setInt(2, season);
            try (ResultSet resultSet = checkForSeason.executeQuery()) {
                if (resultSet.next()) result = true;
            }
            checkForSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getEpisodeID(int showID, int season, int episode) {
        int episodeID = GenericMethods.concatenateDigits(showID, season, episode);
        return doesEpisodeExist(episodeID) ? episodeID : -2;
    }

    public synchronized boolean isEpisodePartOfDoubleEpisode(int showID, int season, int episode) {
        return isEpisodePartOfDoubleEpisode(getEpisodeID(showID, season, episode));
    }

    public synchronized int getEpisodeSeason(int episodeID) {
        if (isNull(getEpisodeSeason))
            getEpisodeSeason = dbManager.prepareStatement(DBStrings.DBShowManager_getEpisodeSeasonSQL);
        int season = -2;
        try {
            getEpisodeSeason.setInt(1, episodeID);
            try (ResultSet resultSet = getEpisodeSeason.executeQuery()) {
                if (resultSet.next()) season = resultSet.getInt(DBStrings.COLUMN_SEASON);
            }
            getEpisodeSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return season;
    }

    public synchronized int doesAlreadyContainShow(String showName) {
        if (isNull(checkForShow)) checkForShow = dbManager.prepareStatement(DBStrings.DBShowManager_checkForShowSQL);
        int showID = -2;
        try {
            checkForShow.setString(1, showName);
            try (ResultSet resultSet = checkForShow.executeQuery()) {
                if (resultSet.next()) showID = resultSet.getInt(DBStrings.COLUMN_SHOW_ID);
            }
            checkForShow.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showID;
    }

    public synchronized Set<Integer> getEpisodeFileDirectories(int episodeID) {
        if (isNull(getEpisodeFileDirectories))
            getEpisodeFileDirectories = dbManager.prepareStatement(DBStrings.DBShowManager_getEpisodeFileDirectoriesSQL);
        Set<Integer> directories = new HashSet<>();
        try {
            getEpisodeFileDirectories.setInt(1, episodeID);
            try (ResultSet resultSet = getEpisodeFileDirectories.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(DBStrings.COLUMN_DIRECTORY_ID));
            }
            getEpisodeFileDirectories.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public synchronized Set<Integer> getAllEpisodeIDsForDirectory(int directoryID) {
        if (isNull(getAllEpisodeIDsForDirectory))
            getAllEpisodeIDsForDirectory = dbManager.prepareStatement(DBStrings.DBShowManager_getAllEpisodeIDsForDirectorySQL);
        Set<Integer> episodeIDsForDirectory = new HashSet<>();
        try {
            getAllEpisodeIDsForDirectory.setInt(1, directoryID);
            try (ResultSet resultSet = getAllEpisodeIDsForDirectory.executeQuery()) {
                while (resultSet.next()) episodeIDsForDirectory.add(resultSet.getInt(DBStrings.COLUMN_EPISODE_ID));
            }
            getAllEpisodeIDsForDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodeIDsForDirectory;
    }

    public synchronized Set<Integer> getAllShowsForDirectory(int directoryID) {
        Set<Integer> showsForDirectory = new HashSet<>();
        getAllEpisodeIDsForDirectory(directoryID).forEach(episodeID -> showsForDirectory.add(getShowIDFromEpisodeID(episodeID)));
        return showsForDirectory;
    }

    public synchronized Set<String> getEpisodeFilesForDirectory(int episodeID, int directoryID) {
        if (isNull(getEpisodeFilesForDirectory))
            getEpisodeFilesForDirectory = dbManager.prepareStatement(DBStrings.DBShowManager_getEpisodeFilesForDirectorySQL);
        Set<String> episodeFiles = new HashSet<>();
        try {
            getEpisodeFilesForDirectory.setInt(1, episodeID);
            getEpisodeFilesForDirectory.setInt(2, directoryID);
            try (ResultSet resultSet = getEpisodeFilesForDirectory.executeQuery()) {
                while (resultSet.next()) episodeFiles.add(resultSet.getString(DBStrings.COLUMN_FILE));
            }
            getEpisodeFilesForDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodeFiles;
    }

    private synchronized boolean verifyEpisodeIDIsInDirectory(int episodeID, int directoryID) {
        if (isNull(verifyEpisodeIDIsInDirectory))
            verifyEpisodeIDIsInDirectory = dbManager.prepareStatement(DBStrings.DBShowManager_verifyEpisodeIDIsInDirectorySQL);
        boolean result = false;
        try {
            verifyEpisodeIDIsInDirectory.setInt(1, episodeID);
            verifyEpisodeIDIsInDirectory.setInt(2, directoryID);
            try (ResultSet resultSet = verifyEpisodeIDIsInDirectory.executeQuery()) {
                result = resultSet.next();
            }
            verifyEpisodeIDIsInDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getAllShowEpisodeIDs(int showID) {
        if (isNull(getAllShowEpisodeIDs))
            getAllShowEpisodeIDs = dbManager.prepareStatement(DBStrings.DBShowManager_getAllShowEpisodeIDsSQL);
        Set<Integer> result = new HashSet<>();
        try {
            getAllShowEpisodeIDs.setInt(1, showID);
            try (ResultSet resultSet = getAllShowEpisodeIDs.executeQuery()) {
                while (resultSet.next()) result.add(resultSet.getInt(DBStrings.COLUMN_EPISODE_ID));
            }
            getAllShowEpisodeIDs.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getDirectoryShowSeasons(int showID, int directoryID) {
        Set<Integer> seasons = new HashSet<>();
        Set<Integer> episodeIDs = new HashSet<>();
        getAllShowEpisodeIDs(showID).forEach(episodeID -> {
            if (verifyEpisodeIDIsInDirectory(episodeID, directoryID)) episodeIDs.add(episodeID);
        });
        episodeIDs.forEach(episodeID -> seasons.add(getEpisodeSeason(episodeID)));
        return seasons;
    }

    public synchronized Set<Integer> getShowDirectories(int showID) {
        Set<Integer> showDirectories = new HashSet<>();
        getSeasons(showID).forEach(season -> getSeasonEpisodes(showID, season).forEach(episode -> showDirectories.addAll(getEpisodeFileDirectories(getEpisodeID(showID, season, episode)))));
        return showDirectories;
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}
