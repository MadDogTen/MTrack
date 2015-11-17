package com.maddogten.mtrack.information.settings;

import com.maddogten.mtrack.util.Variables;

import java.io.Serializable;
import java.util.Map;

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
    @SuppressWarnings("CanBeFinal")
    private Map<String, UserShowSettings> showSettings;

    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal", "unused"})
    private long lastProgramID;

    public UserSettings(String userName, Map<String, UserShowSettings> showSettings, long lastProgramID) {
        this.userName = userName;
        this.userSettingsFileVersion = Variables.UserSettingsFileVersion;
        this.userDirectoryVersion = 1;
        this.showSettings = showSettings;
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
}
