package program.information.settings;

import program.util.Variables;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 349723488242111763L;
    private String userName;

    // Versions
    private int userSettingsFileVersion;
    private int userDirectoryVersion;

    // Show Settings
    private Map<String, UserShowSettings> showSettings;

    public UserSettings(String userName, Map<String, UserShowSettings> showSettings) {
        this.userName = userName;
        this.userSettingsFileVersion = Variables.UserSettingsFileVersion;
        this.userDirectoryVersion = 1;
        this.showSettings = showSettings;
    }

    public void addShowSettings(UserShowSettings userShowSettings) {
        showSettings.put(userShowSettings.getShowName(), userShowSettings);
    }

    // Basic Getters and Setters

    public String getUserName() {
        return userName;
    }

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

    public void setShowSettings(Map<String, UserShowSettings> showSettings) {
        this.showSettings = showSettings;
    }

    public UserShowSettings getAShowSettings(String aShow) {
        return showSettings.get(aShow);
    }
}
