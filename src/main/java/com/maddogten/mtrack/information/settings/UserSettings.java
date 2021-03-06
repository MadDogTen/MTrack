package com.maddogten.mtrack.information.settings;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.Variables;
import com.maddogten.mtrack.util.VideoPlayer;

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
    private VideoPlayer videoPlayer;

    // Changes List
    private String[] changes;
    private Map<String, Integer> changedShowsStatus;

    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal", "unused", "FieldMayBeFinal"})
    private long lastProgramID;

    @SuppressWarnings("SameParameterValue")
    public UserSettings(final String userName, final Map<String, UserShowSettings> showSettings, final boolean showUsername, final VideoPlayer videoPlayer, final String[] changes, final HashMap<String, Integer> changedShowsStatus, final long lastProgramID) {
        this.userName = userName;
        this.userSettingsFileVersion = Variables.UserSettingsFileVersion;
        this.userDirectoryVersion = 1;
        this.showSettings = showSettings;
        this.showUsername = showUsername;
        this.videoPlayer = videoPlayer;
        this.changes = changes;
        this.changedShowsStatus = changedShowsStatus;
        this.lastProgramID = lastProgramID;
    }

    public UserSettings(final String userName, final Map<String, UserShowSettings> showSettings, final VideoPlayer videoPlayer) {
        this.userName = userName;
        this.userSettingsFileVersion = Variables.UserSettingsFileVersion;
        this.userDirectoryVersion = 1;
        this.showSettings = showSettings;
        this.showUsername = true;
        this.videoPlayer = videoPlayer;
        this.changes = new String[0];
        this.changedShowsStatus = new HashMap<>();
        this.lastProgramID = ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID();
    }

    public void addShowSettings(final UserShowSettings userShowSettings) {
        showSettings.put(userShowSettings.getShowName(), userShowSettings);
    }

    // Basic Getters and Setters

    /*public String getUserName() {
        return userName;
    }*/

    public int getUserSettingsFileVersion() {
        return userSettingsFileVersion;
    }

    public void setUserSettingsFileVersion(final int userSettingsFileVersion) {
        this.userSettingsFileVersion = userSettingsFileVersion;
    }

    public int getUserDirectoryVersion() {
        return userDirectoryVersion;
    }

    public void setUserDirectoryVersion(final int userDirectoryVersion) {
        this.userDirectoryVersion = userDirectoryVersion;
    }

    public Map<String, UserShowSettings> getShowSettings() {
        return showSettings;
    }

    public UserShowSettings getAShowSettings(final String aShow) {
        return showSettings.get(aShow);
    }

    public boolean isShowUsername() {
        return showUsername;
    }

    public void setShowUsername(final boolean showUsername) {
        this.showUsername = showUsername;
    }

    public VideoPlayer getVideoPlayer() {
        return videoPlayer;
    }

    public void setVideoPlayer(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    public String[] getChanges() {
        return changes;
    }

    public void setChanges(final String[] changes) {
        this.changes = changes;
    }

    public Map<String, Integer> getChangedShowsStatus() {
        return changedShowsStatus;
    }

    public void setChangedShowsStatus(final Map<String, Integer> changedShowsStatus) {
        this.changedShowsStatus = changedShowsStatus;
    }
}
