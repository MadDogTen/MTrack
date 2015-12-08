package com.maddogten.mtrack.information.settings;

import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.io.Serializable;
import java.util.Random;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class ProgramSettings implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3699693145859359106L;
    private final long programGeneratedID;
    // Versions
    private int programSettingsFileVersion;
    private int mainDirectoryVersion;
    private int showFileVersion;

    // General
    private int updateSpeed;
    private int timeToWaitForDirectory;
    private boolean show0Remaining;
    private String language;

    // ChangeList Settings
    private boolean recordChangesForNonActiveShows;
    private boolean recordChangedSeasonsLowerThanCurrent;

    // Default User
    private boolean useDefaultUser;
    private String defaultUser;

    private int numberOfDirectories;

    // ---- Gui Values ---- \\

    // Default Widths
    private double showColumnWidth;
    private double remainingColumnWidth;
    private double seasonColumnWidth;
    private double episodeColumnWidth;

    // Visibility
    private boolean showColumnVisibility;
    private boolean remainingColumnVisibility;
    private boolean seasonColumnVisibility;
    private boolean episodeColumnVisibility;

    public ProgramSettings() {
        this.programGeneratedID = new Random().nextLong();
        this.programSettingsFileVersion = Variables.ProgramSettingsFileVersion;
        this.mainDirectoryVersion = 0;
        this.showFileVersion = Variables.ShowFileVersion;
        this.updateSpeed = Variables.defaultUpdateSpeed;
        this.timeToWaitForDirectory = Variables.defaultTimeToWaitForDirectory;
        this.show0Remaining = false;
        this.language = "None";
        this.recordChangesForNonActiveShows = false;
        this.recordChangedSeasonsLowerThanCurrent = false;
        this.useDefaultUser = false;
        this.defaultUser = Strings.EmptyString;
        this.showColumnWidth = Variables.SHOWS_COLUMN_WIDTH;
        this.remainingColumnWidth = Variables.REMAINING_COLUMN_WIDTH;
        this.seasonColumnWidth = Variables.SEASONS_COLUMN_WIDTH;
        this.episodeColumnWidth = Variables.EPISODE_COLUMN_WIDTH;
        showColumnVisibility = true;
        remainingColumnVisibility = true;
        seasonColumnVisibility = false;
        episodeColumnVisibility = false;
        this.numberOfDirectories = 0;
    }

    @SuppressWarnings("SameParameterValue")
    public ProgramSettings(int programSettingsFileVersion, int mainDirectoryVersion, int showFileVersion, int updateSpeed, int timeToWaitForDirectory, boolean show0Remaining, String language, boolean recordChangesForNonActiveShows, boolean recordChangedSeasonsLowerThanCurrent, Boolean useDefaultUser, String defaultUser, double showColumnWidth, double remainingColumnWidth, double seasonColumnWidth, double episodeColumnWidth, boolean showColumnVisibility, boolean remainingColumnVisibility, boolean seasonColumnVisibility, boolean episodeColumnVisibility) {
        this.programGeneratedID = new Random().nextLong();
        this.programSettingsFileVersion = programSettingsFileVersion;
        this.mainDirectoryVersion = mainDirectoryVersion;
        this.showFileVersion = showFileVersion;
        this.updateSpeed = updateSpeed;
        this.timeToWaitForDirectory = timeToWaitForDirectory;
        this.show0Remaining = show0Remaining;
        this.language = language;
        this.recordChangesForNonActiveShows = recordChangesForNonActiveShows;
        this.recordChangedSeasonsLowerThanCurrent = recordChangedSeasonsLowerThanCurrent;
        this.useDefaultUser = useDefaultUser;
        this.defaultUser = defaultUser;
        this.showColumnWidth = showColumnWidth;
        this.remainingColumnWidth = remainingColumnWidth;
        this.seasonColumnWidth = seasonColumnWidth;
        this.episodeColumnWidth = episodeColumnWidth;
        this.showColumnVisibility = showColumnVisibility;
        this.remainingColumnVisibility = remainingColumnVisibility;
        this.seasonColumnVisibility = seasonColumnVisibility;
        this.episodeColumnVisibility = episodeColumnVisibility;
        this.numberOfDirectories = 0;
    }

    // Basic Getters and Setters


    public long getProgramSettingsID() {
        return programGeneratedID;
    }

    public int getProgramSettingsFileVersion() {
        return programSettingsFileVersion;
    }

    public void setProgramSettingsFileVersion(int version) {
        programSettingsFileVersion = version;
    }

    public int getMainDirectoryVersion() {
        return mainDirectoryVersion;
    }

    public void setMainDirectoryVersion(int version) {
        mainDirectoryVersion = version;
    }

    public int getShowFileVersion() {
        return showFileVersion;
    }

    public void setShowFileVersion(int version) {
        showFileVersion = version;
    }

    public int getUpdateSpeed() {
        return updateSpeed;
    }

    public void setUpdateSpeed(int updateSpeed) {
        this.updateSpeed = updateSpeed;
    }

    public int getTimeToWaitForDirectory() {
        return timeToWaitForDirectory;
    }

    public void setTimeToWaitForDirectory(int timeToWaitForDirectory) {
        this.timeToWaitForDirectory = timeToWaitForDirectory;
    }

    public boolean isShow0Remaining() {
        return show0Remaining;
    }

    public void setShow0Remaining(boolean show0Remaining) {
        this.show0Remaining = show0Remaining;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isRecordChangesForNonActiveShows() {
        return recordChangesForNonActiveShows;
    }

    public void setRecordChangesForNonActiveShows(boolean recordChangesForNonActiveShows) {
        this.recordChangesForNonActiveShows = recordChangesForNonActiveShows;
    }

    public boolean isRecordChangedSeasonsLowerThanCurrent() {
        return recordChangedSeasonsLowerThanCurrent;
    }

    public void setRecordChangedSeasonsLowerThanCurrent(boolean recordChangedSeasonsLowerThanCurrent) {
        this.recordChangedSeasonsLowerThanCurrent = recordChangedSeasonsLowerThanCurrent;
    }

    public boolean isUseDefaultUser() {
        return useDefaultUser;
    }

    public void setUseDefaultUser(boolean useDefaultUser) {
        this.useDefaultUser = useDefaultUser;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public double getShowColumnWidth() {
        return showColumnWidth;
    }

    public void setShowColumnWidth(double showColumnWidth) {
        this.showColumnWidth = showColumnWidth;
    }

    public double getRemainingColumnWidth() {
        return remainingColumnWidth;
    }

    public void setRemainingColumnWidth(double remainingColumnWidth) {
        this.remainingColumnWidth = remainingColumnWidth;
    }

    public double getSeasonColumnWidth() {
        return seasonColumnWidth;
    }

    public void setSeasonColumnWidth(double seasonColumnWidth) {
        this.seasonColumnWidth = seasonColumnWidth;
    }

    public double getEpisodeColumnWidth() {
        return episodeColumnWidth;
    }

    public void setEpisodeColumnWidth(double episodeColumnWidth) {
        this.episodeColumnWidth = episodeColumnWidth;
    }

    public boolean isShowColumnVisibility() {
        return showColumnVisibility;
    }

    public void setShowColumnVisibility(boolean showColumnVisibility) {
        this.showColumnVisibility = showColumnVisibility;
    }

    public boolean isRemainingColumnVisibility() {
        return remainingColumnVisibility;
    }

    public void setRemainingColumnVisibility(boolean remainingColumnVisibility) {
        this.remainingColumnVisibility = remainingColumnVisibility;
    }

    public boolean isSeasonColumnVisibility() {
        return seasonColumnVisibility;
    }

    public void setSeasonColumnVisibility(boolean seasonColumnVisibility) {
        this.seasonColumnVisibility = seasonColumnVisibility;
    }

    public boolean isEpisodeColumnVisibility() {
        return episodeColumnVisibility;
    }

    public void setEpisodeColumnVisibility(boolean episodeColumnVisibility) {
        this.episodeColumnVisibility = episodeColumnVisibility;
    }

    public int getNumberOfDirectories() {
        return numberOfDirectories;
    }

    public void setNumberOfDirectories(int numberOfDirectories) {
        this.numberOfDirectories = numberOfDirectories;
    }
}
