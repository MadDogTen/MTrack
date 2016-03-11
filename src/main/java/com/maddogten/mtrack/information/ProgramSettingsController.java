package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.information.settings.ProgramSettings;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.util.logging.Logger;

/*
      ProgramSettingsController loads and stores the ProgramSettings.
 */

public class ProgramSettingsController {
    private final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());
    private final UserInfoController userInfoController;
    private ProgramSettings settingsFile;
    private boolean mainDirectoryVersionAlreadyChanged = false;

    @SuppressWarnings("SameParameterValue")
    public ProgramSettingsController(UserInfoController userInfoController) {
        this.userInfoController = userInfoController;
    }

    public void loadProgramSettingsFile() {
        this.settingsFile = (ProgramSettings) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingFileExtension);
    }

    public void setDefaultLanguage(String language) {
        settingsFile.setLanguage(language);
        log.info("Default language was set to " + language + '.');
    }

    public void setUpdateSpeed(int updateSpeed) {
        settingsFile.setUpdateSpeed(updateSpeed);
        log.info("Update speed is now set to: " + updateSpeed);
    }

    public void setSavingSpeed(int savingSpeed) {
        settingsFile.setSaveSpeed(savingSpeed);
        log.info("Save speed is now set to: " + savingSpeed);
    }

    public void setTimeToWaitForDirectory(int timeToWaitForDirectory) {
        settingsFile.setTimeToWaitForDirectory(timeToWaitForDirectory);
        log.info("Time to wait for directory is now set to: " + timeToWaitForDirectory);
    }

    public void setDefaultUsername(String userName, boolean useDefaultUser) {
        log.info("DefaultUsername is being set...");
        settingsFile.setUseDefaultUser(useDefaultUser);
        settingsFile.setDefaultUser(userName);
        log.info("DefaultUsername is set as " + userName + '.');
    }

    public void setMainDirectoryVersion(int version) {
        if (mainDirectoryVersionAlreadyChanged) {
            log.info("Already changed main directory version this run, no further change needed.");
        } else {
            settingsFile.setMainDirectoryVersion(version);
            // Current User should always be up to date, so its version can be updated with the Main Directory Version.
            if (!Main.getMainRun().firstRun) userInfoController.getUserSettings().setUserDirectoryVersion(version);
            saveSettingsFile();
            log.info("Main + User directory version updated to: " + version);
            mainDirectoryVersionAlreadyChanged = true;
        }
    }

    public ProgramSettings getSettingsFile() {
        return settingsFile;
    }

    public void setSettingsFile(ProgramSettings settingsFile) {
        this.settingsFile = settingsFile;
    }

    // Save the file
    public void saveSettingsFile() {
        new FileManager().save(settingsFile, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingFileExtension, true);
        log.info("settingsFile has been saved!");
    }
}
