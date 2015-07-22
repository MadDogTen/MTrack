package com.maddogten.mtrack.information.settings;

import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class ProgramSettings implements Serializable {

    private static final long serialVersionUID = 3699693145859359106L;
    // Versions
    private int programSettingsFileVersion;
    private int mainDirectoryVersion;
    private int showFileVersion;

    // General
    private int updateSpeed;
    private boolean show0Remaining;
    private String language;

    // Default User
    private boolean useDefaultUser;
    private String defaultUser;

    // Directories
    private ArrayList<String> directories;


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
        this.programSettingsFileVersion = Variables.ProgramSettingsFileVersion;
        this.mainDirectoryVersion = 0;
        this.showFileVersion = Variables.ShowFileVersion;
        this.updateSpeed = Variables.defaultUpdateSpeed;
        this.show0Remaining = false;
        this.language = "None";
        this.useDefaultUser = false;
        this.defaultUser = Strings.EmptyString;
        this.directories = new ArrayList<>();
        this.showColumnWidth = Variables.SHOWS_COLUMN_WIDTH;
        this.remainingColumnWidth = Variables.REMAINING_COLUMN_WIDTH;
        this.seasonColumnWidth = Variables.SEASONS_COLUMN_WIDTH;
        this.episodeColumnWidth = Variables.EPISODE_COLUMN_WIDTH;
        showColumnVisibility = true;
        remainingColumnVisibility = true;
        seasonColumnVisibility = false;
        episodeColumnVisibility = false;
    }

    public ProgramSettings(int programSettingsFileVersion, int mainDirectoryVersion, int showFileVersion, int updateSpeed, boolean show0Remaining, String language, Boolean useDefaultUser, String defaultUser, ArrayList<String> directories, double showColumnWidth, double remainingColumnWidth, double seasonColumnWidth, double episodeColumnWidth, boolean showColumnVisibility, boolean remainingColumnVisibility, boolean seasonColumnVisibility, boolean episodeColumnVisibility) {
        this.programSettingsFileVersion = programSettingsFileVersion;
        this.mainDirectoryVersion = mainDirectoryVersion;
        this.showFileVersion = showFileVersion;
        this.updateSpeed = updateSpeed;
        this.show0Remaining = show0Remaining;
        this.language = language;
        this.useDefaultUser = useDefaultUser;
        this.defaultUser = defaultUser;
        this.directories = directories;
        this.showColumnWidth = showColumnWidth;
        this.remainingColumnWidth = remainingColumnWidth;
        this.seasonColumnWidth = seasonColumnWidth;
        this.episodeColumnWidth = episodeColumnWidth;
        this.showColumnVisibility = showColumnVisibility;
        this.remainingColumnVisibility = remainingColumnVisibility;
        this.seasonColumnVisibility = seasonColumnVisibility;
        this.episodeColumnVisibility = episodeColumnVisibility;
    }

    public String getDirectory(int index) {
        return directories.get(index);
    }

    public void removeDirectory(String directory) {
        directories.remove(directory);
    }

    // Basic Getters and Setters

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

    public ArrayList<String> getDirectories() {
        return directories;
    }

    public void setDirectories(ArrayList<String> directories) {
        this.directories = directories;
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
}
