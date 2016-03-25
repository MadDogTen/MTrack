package com.maddogten.mtrack.information.settings;

import com.maddogten.mtrack.util.Variables;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
      UserSettings stores all the user settings, including a Map that stores show names and their related settings.
 */

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class UserSettings implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 349723488242111763L;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String userName; // Final for now, May add the ability to change it later

    // Versions
    private int userSettingsFileVersion;
    private int userDirectoryVersion;

    // Show Settings
    @SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
    private Map<String, UserShowSettings> showSettings;

    // Options
    private boolean showUsername;

    // Changes List
    private String[] changes;
    private Map<String, Integer> changedShowsStatus;

    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal", "unused", "FieldMayBeFinal"})
    private long lastProgramID;

    @SuppressWarnings("SameParameterValue")
    public UserSettings(String userName, Map<String, UserShowSettings> showSettings, boolean showUsername, String[] changes, HashMap<String, Integer> changedShowsStatus, long lastProgramID) {
        this.userName = userName;
        this.userSettingsFileVersion = Variables.UserSettingsFileVersion;
        this.userDirectoryVersion = 1;
        this.showSettings = showSettings;
        this.showUsername = showUsername;
        this.changes = changes;
        this.changedShowsStatus = changedShowsStatus;
        this.lastProgramID = lastProgramID;
    }

    public void addShowSettings(UserShowSettings userShowSettings) {
        showSettings.put(userShowSettings.getShowName(), userShowSettings);
    }

    // Basic Getters and Setters

    /*public String getUserName() {
        return userName;
    }*/

    public int getUserSettingsFileVersion() {
        return userSettingsFileVersion;
    }

    public void setUserSettingsFileVersion(int userSettingsFileVersion) {
        this.userSettingsFileVersion = userSettingsFileVersion;
    }

    public int getUserDirectoryVersion() {
        return userDirectoryVersion;
    }

    public void setUserDirectoryVersion(int userDirectoryVersion) {
        this.userDirectoryVersion = userDirectoryVersion;
    }

    public Map<String, UserShowSettings> getShowSettings() {
        return showSettings;
    }

    public UserShowSettings getAShowSettings(String aShow) {
        return showSettings.get(aShow);
    }

    public boolean isShowUsername() {
        return showUsername;
    }

    public void setShowUsername(boolean showUsername) {
        this.showUsername = showUsername;
    }

    public String[] getChanges() {
        return changes;
    }

    public void setChanges(String[] changes) {
        this.changes = changes;
    }

    public Map<String, Integer> getChangedShowsStatus() {
        return changedShowsStatus;
    }

    public void setChangedShowsStatus(Map<String, Integer> changedShowsStatus) {
        this.changedShowsStatus = changedShowsStatus;
    }
}
