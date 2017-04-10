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
    private final PreparedStatement addSeason;
    private final PreparedStatement addEpisode;
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

    public DBShowManager(Connection connection) throws SQLException {
        initTables(connection);

        getAllShows = connection.prepareStatement("SELECT " + StringDB.showID + " FROM " + StringDB.shows);
        addShow = connection.prepareStatement("INSERT INTO " + StringDB.shows + " VALUES (?, ?)");
        addSeason = connection.prepareStatement("INSERT INTO " + StringDB.seasons + " VALUES (?, ?)");
        addEpisode = connection.prepareStatement("INSERT INTO " + StringDB.episodes + " VALUES (?, ?, ?, ?, ?)");
        checkShowID = connection.prepareStatement("SELECT " + StringDB.showName + " FROM " + StringDB.shows + " WHERE " + StringDB.showID + "=?");
        getShowID = connection.prepareStatement("SELECT " + StringDB.showID + " FROM " + StringDB.shows + " WHERE " + StringDB.showName + " =?");
        checkForShow = connection.prepareStatement("SELECT * FROM " + StringDB.shows + " WHERE " + StringDB.showName + "=?");
        getSeasons = connection.prepareStatement("SELECT * FROM " + StringDB.seasons + " WHERE " + StringDB.showID + "=?");
        getSeasonEpisodes = connection.prepareStatement("SELECT * FROM " + StringDB.episodes + " WHERE " + StringDB.showID + "=? AND " + StringDB.season + "=?");
        getEpisode = connection.prepareStatement("SELECT * FROM " + StringDB.episodeFiles + " WHERE " + StringDB.episodeID + "=?");
        isEpisodePartOfDoubleEpisode = connection.prepareStatement("SELECT " + StringDB.partOfDoubleEpisode + " FROM " + StringDB.episodes + " WHERE " + StringDB.showID + "=? AND " + StringDB.season + "=? AND " + StringDB.episode + "=?");
//        updateEpisodeFile = connection.prepareStatement("UPDATE " + StringDB.episodes + " SET " + StringDB.file + "=? WHERE " + StringDB.showID + "=? AND " + StringDB.season + "=? AND " + StringDB.episode + "=?");
        addEpisodeFile = connection.prepareStatement("INSERT INTO " + StringDB.episodeFiles + " VALUES (?, ?, ?)");
        doesContainEpisode = connection.prepareStatement("SELECT " + StringDB.episodeID + " FROM " + StringDB.episodes + " WHERE " + StringDB.episodeID + "=?");
        removeEpisodeFile = connection.prepareStatement("DELETE FROM " + StringDB.episodeFiles + " WHERE " + StringDB.episodeID + "=? AND " + StringDB.directoryID + "=?" + " AND " + StringDB.file + "=?");
        getShowName = connection.prepareStatement("SELECT " + StringDB.showName + " FROM " + StringDB.shows + " WHERE " + StringDB.showID + "=?");
    }

    private void initTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.shows, null)) {
                if (!resultSet.next()) {
                    log.fine("Shows table doesn't exist, creating...");
                    createShowsTable(statement);
                }
            }
            try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.seasons, null)) {
                if (!resultSet.next()) {
                    log.fine("Seasons table doesn't exist, creating...");
                    createSeasonsTable(statement);
                }
            }
            try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.episodes, null)) {
                if (!resultSet.next()) {
                    log.fine("Episodes table doesn't exist, creating...");
                    createEpisodesTable(statement);
                }
            }
            try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.episodeFiles, null)) {
                if (!resultSet.next()) {
                    log.fine("Episode Files Table doesn't exist, creating...");
                    createEpisodeFilesTable(statement);
                }
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private void createEpisodeFilesTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.episodeFiles + "(" + StringDB.episodeID + " INTEGER NOT NULL, " + StringDB.directoryID + " INTEGER NOT NULL, " + StringDB.file + " VARCHAR(200) NOT NULL)");
    }

    private void createShowsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.shows + "(" + StringDB.showID + " INTEGER UNIQUE NOT NULL, " + StringDB.showName + " VARCHAR(50) UNIQUE NOT NULL)");
    }

    private void createSeasonsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.seasons + "(" + StringDB.showID + " INTEGER NOT NULL, " + StringDB.season + " INTEGER NOT NULL)");
    }

    private void createEpisodesTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.episodes + "(" + StringDB.showID + " INTEGER NOT NULL, " + StringDB.season + " INTEGER NOT NULL, " + StringDB.episode + " INTEGER NOT NULL, " + StringDB.partOfDoubleEpisode + " BOOLEAN NOT NULL, episodeID INTEGER NOT NULL)");
    }

    public ArrayList<Integer> getAllShows() {
        ArrayList<Integer> allShows = new ArrayList<>();
        try {
            try (ResultSet resultSet = getAllShows.executeQuery()) {
                while (resultSet.next()) allShows.add(resultSet.getInt(StringDB.showID));
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
                while (resultSet.next()) showName = resultSet.getString(StringDB.showName);
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
            if (resultSet.next()) result = resultSet.getInt(StringDB.showID);
            else log.warning("Couldn't find ShowID for \"" + showName + "\".");
        }
        getShowID.clearParameters();
        return result;
    }

    public boolean doesAlreadyContainsShow(String showName) throws SQLException {
        checkForShow.setString(1, showName);
        try (ResultSet resultSet = checkForShow.executeQuery()) {
            boolean result = resultSet.next();
            checkForShow.clearParameters();
            return result;
        }
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

    public boolean addShow(String show) throws SQLException {
        if (doesAlreadyContainsShow(show)) return false;
        addShow.setInt(1, generateShowID());
        addShow.setString(2, show);
        addShow.execute();
        addShow.clearParameters();
        return true;
    }

    public Set<Integer> getSeasons(int showID) {
        Set<Integer> seasons = new HashSet<>();
        try {
            getSeasons.setInt(1, showID);
            try (ResultSet resultSet = getSeasons.executeQuery()) {
                while (resultSet.next()) seasons.add(resultSet.getInt(StringDB.season));
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
                while (resultSet.next()) episodes.add(resultSet.getInt(StringDB.episode));
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

    public File getEpisodeFile(int showID, int season, int episode) {
        File episodeFile = null;
        try {
            getEpisode.setInt(1, getEpisodeID(showID, season, episode));
            ResultSet resultSet = getEpisode.executeQuery();
            int i = 0;
            while (resultSet.next()) i++;
            if (i > 0) {
                if (i == 1) {
                    resultSet.first();
                    episodeFile = new File(resultSet.getString(StringDB.file));
                } else {
                    DBDirectoryHandler dbDirectoryHandler = ClassHandler.getDBManager().getDbDirectoryHandler();
                    int lowestPriorityRow = -2, lowestPriority = -2, currentRow = -2;
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        currentRow = resultSet.getRow();
                        int currentPriority = dbDirectoryHandler.getDirectoryPriority(resultSet.getInt(StringDB.directoryPriority));
                        if (lowestPriority == -2 || currentPriority < lowestPriority) {
                            lowestPriority = currentPriority;
                            lowestPriorityRow = currentRow;
                        }
                    }
                    resultSet.absolute(lowestPriorityRow);
                    episodeFile = new File(resultSet.getString(StringDB.file));
                }
            }
            getEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodeFile;
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

    public void addEpisode(int showID, int season, int episode, Boolean partOfDoubleSeason, String... files) {
        if (!doesContainEpisode(getEpisodeID(showID, season, episode))) {
            try {
                addEpisode.setInt(1, showID);
                addEpisode.setInt(2, season);
                addEpisode.setInt(3, episode);
                addEpisode.setBoolean(4, partOfDoubleSeason);
                addEpisode.setInt(5, getEpisodeID(showID, season, episode));
                addEpisode.execute();
                addEpisode.clearParameters();

                for (String file : files) { // TODO Correct, This is Temp, Non Functional
                    addEpisodeFile.setInt(1, showID);
                    addEpisodeFile.setInt(2, 0);
                    addEpisodeFile.setString(3, file);
                    addEpisodeFile.execute();
                    addEpisodeFile.clearParameters();
                }
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        } else {
            // Check if any of the files are new, and if so, add them.
        }
    }

    public void addEpisodeFile(int showID, int season, int episode, int directoryID, String episodeFile) {
        try {
            addEpisodeFile.setInt(1, getEpisodeID(showID, season, episode));
            addEpisodeFile.setInt(2, directoryID);
            addEpisodeFile.setString(3, episodeFile);
            addEpisodeFile.execute();
            addEpisodeFile.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void removeEpisodeFile(int showID, int season, int episode, int directoryID, String episodeFile) {
        try {
            removeEpisodeFile.setInt(1, getEpisodeID(showID, season, episode));
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

    private int getEpisodeID(int showID, int season, int episode) {
        return GenericMethods.concatenateDigits(showID, season, episode);
    }
}
