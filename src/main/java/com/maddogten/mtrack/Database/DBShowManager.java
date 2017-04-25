package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;
import com.maddogten.mtrack.util.Strings;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class DBShowManager {
    private final Logger log = Logger.getLogger(DBShowManager.class.getName());

    private final PreparedStatement getAllShows;
    private final PreparedStatement addShow;
    private final PreparedStatement removeShow;
    private final PreparedStatement addSeason;
    private final PreparedStatement removeSeasons;
    private final PreparedStatement addEpisode;
    private final PreparedStatement removeEpisodes;
    private final PreparedStatement removeEpisodeFiles;
    private final PreparedStatement checkShowID;
    private final PreparedStatement getShowID;
    private final PreparedStatement checkForShow;
    private final PreparedStatement getSeasons;
    private final PreparedStatement getSeasonEpisodes;
    private final PreparedStatement getEpisode;
    private final PreparedStatement isEpisodePartOfDoubleEpisode;
    // private final PreparedStatement updateEpisodeFile;
    private final PreparedStatement addEpisodeFile;
    private final PreparedStatement doesContainEpisode;
    private final PreparedStatement removeEpisodeFile;
    private final PreparedStatement getShowName;
    private final PreparedStatement getShowIDFromEpisodeID;
    private final PreparedStatement getEpisodeDirectories;
    private final PreparedStatement checkForSeason;
    private final PreparedStatement getEpisodeSeason;

    public DBShowManager(Connection connection) throws SQLException {
        initTables(connection);

        getAllShows = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_SHOWS);
        addShow = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_SHOWS + " VALUES (?, ?)");
        removeShow = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_SHOWS + " WHERE " + StringDB.COLUMN_SHOW_ID + "=?");
        addSeason = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_SEASONS + " VALUES (?, ?)");
        removeSeasons = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_SEASONS + " WHERE " + StringDB.COLUMN_SHOW_ID + "=?");
        addEpisode = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_EPISODES + " VALUES (?, ?, ?, ?, ?)");
        removeEpisodes = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_EPISODES + " WHERE " + StringDB.COLUMN_SHOW_ID + "=?");
        removeEpisodeFiles = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_EPISODEFILES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=?");
        checkShowID = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWNAME + " FROM " + StringDB.TABLE_SHOWS + " WHERE " + StringDB.COLUMN_SHOW_ID + "=?");
        getShowID = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_SHOWS + " WHERE " + StringDB.COLUMN_SHOWNAME + " =?");
        checkForShow = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_SHOWS + " WHERE " + StringDB.COLUMN_SHOWNAME + "=?");
        getSeasons = connection.prepareStatement("SELECT * FROM " + StringDB.TABLE_SEASONS + " WHERE " + StringDB.COLUMN_SHOW_ID + "=?");
        getSeasonEpisodes = connection.prepareStatement("SELECT * FROM " + StringDB.TABLE_EPISODES + " WHERE " + StringDB.COLUMN_SHOW_ID + "=? AND " + StringDB.COLUMN_SEASON + "=?");
        getEpisode = connection.prepareStatement("SELECT " + StringDB.COLUMN_FILE + " FROM " + StringDB.TABLE_EPISODEFILES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=? AND " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        isEpisodePartOfDoubleEpisode = connection.prepareStatement("SELECT " + StringDB.COLUMN_PARTOFDOUBLEEPISODE + " FROM " + StringDB.TABLE_EPISODES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=?");
//        updateEpisodeFile = connection.prepareStatement("UPDATE " + StringDB.episodes + " SET " + StringDB.file + "=? WHERE " + StringDB.showID + "=? AND " + StringDB.season + "=? AND " + StringDB.episode + "=?");
        addEpisodeFile = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_EPISODEFILES + " VALUES (?, ?, ?)");
        doesContainEpisode = connection.prepareStatement("SELECT " + StringDB.COLUMN_EPISODE_ID + " FROM " + StringDB.TABLE_EPISODES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=?");
        removeEpisodeFile = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_EPISODEFILES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=? AND " + StringDB.COLUMN_DIRECTORY_ID + "=?" + " AND " + StringDB.COLUMN_FILE + "=?");
        getShowName = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWNAME + " FROM " + StringDB.TABLE_SHOWS + " WHERE " + StringDB.COLUMN_SHOW_ID + "=?");
        getShowIDFromEpisodeID = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_EPISODES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=?");
        getEpisodeDirectories = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY_ID + " FROM " + StringDB.TABLE_EPISODEFILES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=?");
        checkForSeason = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_SEASONS + " WHERE " + StringDB.COLUMN_SHOW_ID + "=? AND " + StringDB.COLUMN_SEASON + "=?");
        getEpisodeSeason = connection.prepareStatement("SELECT " + StringDB.COLUMN_SEASON + " FROM " + StringDB.TABLE_EPISODES + " WHERE " + StringDB.COLUMN_EPISODE_ID + "=?");
    }

    private void initTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            createShowsTable(statement);
            createSeasonsTable(statement);
            createEpisodesTable(statement);
            createEpisodeFilesTable(statement);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private void createEpisodeFilesTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_EPISODEFILES + "(" + StringDB.COLUMN_EPISODE_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_DIRECTORY_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_FILE + " VARCHAR(200) NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private void createShowsTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_SHOWS + "(" + StringDB.COLUMN_SHOW_ID + " INTEGER UNIQUE NOT NULL, " + StringDB.COLUMN_SHOWNAME + " VARCHAR(50) UNIQUE NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    } // TODO Add option to change the show name displayed

    private void createSeasonsTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_SEASONS + "(" + StringDB.COLUMN_SHOW_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_SEASON + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private void createEpisodesTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_EPISODES + "(" + StringDB.COLUMN_SHOW_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_SEASON + " INTEGER NOT NULL, " + StringDB.COLUMN_EPISODE + " INTEGER NOT NULL, " + StringDB.COLUMN_PARTOFDOUBLEEPISODE + " BOOLEAN NOT NULL, " + StringDB.COLUMN_EPISODE_ID + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public ArrayList<Integer> getAllShows() {
        ArrayList<Integer> allShows = new ArrayList<>();
        try {
            try (ResultSet resultSet = getAllShows.executeQuery()) {
                while (resultSet.next()) allShows.add(resultSet.getInt(StringDB.COLUMN_SHOW_ID));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return allShows;
    }

    public ArrayList<String> getAllShowStrings() { // TODO Remove, Temp Value
        ArrayList<String> allShows = new ArrayList<>();
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT " + StringDB.COLUMN_SHOWNAME + " FROM " + StringDB.TABLE_SHOWS)) {
                while (resultSet.next()) allShows.add(resultSet.getString(StringDB.COLUMN_SHOWNAME));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return allShows;
    }

    public String getShowName(int showID) {
        String showName = Strings.EmptyString;
        try {
            getShowName.setInt(1, showID);
            try (ResultSet resultSet = getShowName.executeQuery()) {
                if (resultSet.next()) showName = resultSet.getString(StringDB.COLUMN_SHOWNAME);
            }
            getShowName.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showName;
    }

    public String getShowNameFromEpisodeID(int episodeID) {
        String showName = Strings.EmptyString;
        try {
            getShowName.setInt(1, episodeID);
            try (ResultSet resultSet = getShowName.executeQuery()) {
                if (resultSet.next()) showName = getShowName(resultSet.getInt(StringDB.COLUMN_EPISODE_ID));
            }
            getShowName.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showName;
    }

    public int getShowID(String showName) throws SQLException {
        getShowID.setString(1, showName);
        int result = -2;
        try (ResultSet resultSet = getShowID.executeQuery()) {
            if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_SHOW_ID);
            else log.warning("Couldn't find ShowID for \"" + showName + "\".");
        }
        getShowID.clearParameters();
        return result;
    }

    public boolean isEpisodePartOfDoubleEpisode(int episodeID) {
        boolean result = false;
        try {
            isEpisodePartOfDoubleEpisode.setInt(1, episodeID);
            try (ResultSet resultSet = isEpisodePartOfDoubleEpisode.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_PARTOFDOUBLEEPISODE);
            }
            isEpisodePartOfDoubleEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private int generateShowID() throws SQLException {
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

    public int addShow(String show) {
        int showID = doesAlreadyContainShow(show);
        if (showID != -2) return showID;
        else try {
            showID = generateShowID();
            addShow.setInt(1, showID);
            addShow.setString(2, show);
            addShow.execute();
            addShow.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showID;
    }

    public void removeShow(int showID) {
        try {
            removeShow.setInt(1, showID);
            removeShow.execute();
            removeShow.clearParameters();
            Set<Integer> seasons = getSeasons(showID);
            removeSeasons.setInt(1, showID);
            removeSeasons.execute();
            removeSeasons.clearParameters();
            seasons.forEach(season -> getSeasonEpisodes(showID, season).forEach(this::removeEpisodeFiles));
            removeEpisodes.setInt(1, showID);
            removeEpisodes.execute();
            removeEpisodes.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public Set<Integer> getSeasons(int showID) {
        Set<Integer> seasons = new HashSet<>();
        try {
            getSeasons.setInt(1, showID);
            try (ResultSet resultSet = getSeasons.executeQuery()) {
                while (resultSet.next()) seasons.add(resultSet.getInt(StringDB.COLUMN_SEASON));
            }
            getSeasons.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return seasons;
    }

    public Set<Integer> getSeasonEpisodes(int showID, int season) {
        Set<Integer> episodes = new HashSet<>();
        try {
            getSeasonEpisodes.setInt(1, showID);
            getSeasonEpisodes.setInt(2, season);
            try (ResultSet resultSet = getSeasonEpisodes.executeQuery()) {
                while (resultSet.next()) episodes.add(resultSet.getInt(StringDB.COLUMN_EPISODE));
            }
            getSeasonEpisodes.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodes;
    }

    public void addSeason(int showID, int season) {
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

    public File getEpisode(int episodeID) {
        File episodeFile = null;
        try {
            getEpisodeDirectories.setInt(1, episodeID);
            Set<Integer> directories = new HashSet<>();
            try (ResultSet resultSet = getEpisodeDirectories.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(StringDB.COLUMN_DIRECTORY_ID));
            }
            getEpisodeDirectories.clearParameters();
            if (!directories.isEmpty()) {
                String showName = getShowNameFromEpisodeID(episodeID);
                int season = getEpisodeSeason(episodeID);
                if (directories.size() == 1) {
                    getEpisode.setInt(1, episodeID);
                    int directoryID = -2;
                    for (int id : directories) {
                        getEpisode.setInt(2, id);
                        directoryID = id;
                        break;
                    }
                    File mainFolder = ClassHandler.directoryController().getDirectoryFromID(directoryID);
                    try (ResultSet resultSet = getEpisode.executeQuery()) {
                        if (resultSet.next())
                            episodeFile = new File(mainFolder + Strings.FileSeparator + GenericMethods.getSeasonFolderName(mainFolder, showName, season) + Strings.FileSeparator + resultSet.getString(StringDB.COLUMN_FILE));
                    }
                    getEpisode.clearParameters();
                } else {
                    getEpisode.setInt(1, episodeID);
                    int directoryID = ClassHandler.directoryController().getDirectoryWithLowestPriority(directories);
                    getEpisode.setInt(2, directoryID);
                    File mainFolder = ClassHandler.directoryController().getDirectoryFromID(directoryID);
                    try (ResultSet resultSet = getEpisode.executeQuery()) {
                        if (resultSet.next())
                            episodeFile = new File(mainFolder + Strings.FileSeparator + GenericMethods.getSeasonFolderName(mainFolder, showName, season) + Strings.FileSeparator + resultSet.getString(StringDB.COLUMN_FILE));
                    }
                    getEpisode.clearParameters();
                }
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodeFile.exists() ? episodeFile : null;
    }

    private boolean doesContainEpisode(int episodeID) {
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

    public int addEpisode(int showID, int season, int episode, Boolean partOfDoubleEpisode) {
        int episodeID = -2;
        if (!doesContainEpisode(getEpisodeID(showID, season, episode))) {
            try {
                addEpisode.setInt(1, showID);
                addEpisode.setInt(2, season);
                addEpisode.setInt(3, episode);
                addEpisode.setBoolean(4, partOfDoubleEpisode);
                episodeID = getEpisodeID(showID, season, episode);
                addEpisode.setInt(5, episodeID);
                addEpisode.execute();
                addEpisode.clearParameters();
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        return episodeID;
    }

    public void addEpisodeFile(int episodeID, int directoryID, String episodeFile) {
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

    public void removeEpisodeFiles(int episodeID) {
        try {
            removeEpisodeFiles.setInt(1, episodeID);
            removeEpisodeFile.execute();
            removeEpisodeFile.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void removeEpisodeFile(int episodeID, int directoryID, String episodeFile) {
        try {
            removeEpisodeFile.setInt(1, episodeID);
            removeEpisodeFile.setInt(2, directoryID);
            removeEpisodeFile.setString(3, episodeFile);
            removeEpisodeFile.execute();
            removeEpisodeFile.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    /*public void updateEpisodeFile(int showID, int season, int episode, String newFile) {
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

    public boolean doesEpisodeExist(int showID, int season, int episode) {
        return getEpisode(getEpisodeID(showID, season, episode)) != null;
    }

    public boolean doesSeasonExist(int showID, int season) {
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

    public int getEpisodeID(int showID, int season, int episode) {
        return GenericMethods.concatenateDigits(showID, season, episode);
    }

    public boolean isEpisodePartOfDoubleEpisode(int showID, int season, int episode) {
        return isEpisodePartOfDoubleEpisode(getEpisodeID(showID, season, episode));
    }

    public int getEpisodeSeason(int episodeID) {
        int season = -2;
        try {
            getEpisodeSeason.setInt(1, episodeID);
            try (ResultSet resultSet = getEpisodeSeason.executeQuery()) {
                if (resultSet.next()) season = resultSet.getInt(StringDB.COLUMN_SEASON);
            }
            getEpisodeSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return season;
    }

    public int doesAlreadyContainShow(String showName) {
        int showID = -2;
        try {
            checkForShow.setString(1, showName);
            try (ResultSet resultSet = checkForShow.executeQuery()) {
                if (resultSet.next()) showID = resultSet.getInt(StringDB.COLUMN_SHOW_ID);
            }
            checkForShow.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return showID;
    }
}
