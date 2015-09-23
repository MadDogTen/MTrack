package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.information.settings.ProgramSettings;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.util.logging.Logger;

public class ProgramSettingsController {
    private final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());
    private final UserInfoController userInfoController;
    private ProgramSettings settingsFile;
    private boolean mainDirectoryVersionAlreadyChanged = false;

    @SuppressWarnings("SameParameterValue")
    public ProgramSettingsController(UserInfoController userInfoController) {
        this.userInfoController = userInfoController;
    }

    @SuppressWarnings("unchecked")
    public void loadProgramSettingsFile() {
        this.settingsFile = (ProgramSettings) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
    }


    // The update speed is used for mainRun tick to only run after this set amount of time goes by. Saved in seconds.
    public int getUpdateSpeed() {
        return settingsFile.getUpdateSpeed();
    }

    public void setUpdateSpeed(int updateSpeed) {
        settingsFile.setUpdateSpeed(updateSpeed);
        Variables.setUpdateSpeed(updateSpeed);
        log.info("Update speed is now set to: " + updateSpeed);
    }

    // Show0Remaining is used in the Controller to save the state of the checkbox at shutdown. Toggles whether or not the program has shows with 0 episode remaining visible.
    public boolean getShow0Remaining() {
        return settingsFile.isShow0Remaining();
    }

    public void setShow0Remaining(boolean show0Remaining) {
        settingsFile.setShow0Remaining(show0Remaining);
    }

    // Language saves which language the user chooses. Save the internal name, Not the user friendly name.
    public String getLanguage() {
        return settingsFile.getLanguage();
    }

    public void setLanguage(String language) {
        settingsFile.setLanguage(language);
    }

    // DefaultUsername save the user that was chosen to automatically start with.
    public boolean isDefaultUsername() {
        return settingsFile.isUseDefaultUser();
    }

    public String getDefaultUsername() {
        return settingsFile.getDefaultUser();
    }

    public void setDefaultUsername(String userName, boolean useDefaultUser) {
        log.info("DefaultUsername is being set...");
        settingsFile.setUseDefaultUser(useDefaultUser);
        settingsFile.setDefaultUser(userName);
        log.info("DefaultUsername is set.");
    }

    // All versions are used in UpdateManager to update the files to the latest version if needed.
    public int getProgramSettingsFileVersion() {
        return settingsFile.getProgramSettingsFileVersion();
    }

    public int getMainDirectoryVersion() {
        return settingsFile.getMainDirectoryVersion();
    }

    public void setMainDirectoryVersion(int version) {
        if (mainDirectoryVersionAlreadyChanged) {
            log.info("Already changed main directory version this run, no further change needed.");
        } else {
            settingsFile.setMainDirectoryVersion(version);
            // Current User should always be up to date, so its version can be updated with the Main Directory Version.
            if (!Main.getMainRun().firstRun) {
                userInfoController.setUserDirectoryVersion(version);
            }

            saveSettingsFile();
            log.info("Main + User directory version updated to: " + version);
            mainDirectoryVersionAlreadyChanged = true;
        }
    }

    public int getNumberOfDirectories() {
        return settingsFile.getNumberOfDirectories();
    }

    public void setNumberOfDirectories(int numberOfDirectories) {
        settingsFile.setNumberOfDirectories(numberOfDirectories);
    }

    public int getShowFileVersion() {
        return settingsFile.getShowFileVersion();
    }

    public void setShowFileVersion(int version) {
        settingsFile.setShowFileVersion(version);
    }

    public long getProgramGeneratedID() {
        return settingsFile.getProgramSettingsID();
    }

    public ProgramSettings getSettingsFile() {
        return settingsFile;
    }

    public void setSettingsFile(ProgramSettings settingsFile) {
        this.settingsFile = settingsFile;
    }

    // Save the file
    public void saveSettingsFile() {
        if (settingsFile != null) {
            new FileManager().save(settingsFile, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("settingsFile has been saved!");
        }
    }
}
