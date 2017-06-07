package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Database.DBManager;
import com.maddogten.mtrack.Database.DBProgramSettingsManager;

import java.sql.SQLException;
import java.util.logging.Logger;

/*
      ProgramSettingsController loads and stores the ProgramSettings.
 */

public class ProgramSettingsController {
    private final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());
    private DBProgramSettingsManager dbProgramSettingsManager;

    public void initDatabase(DBManager dbManager) throws SQLException {
        dbProgramSettingsManager = new DBProgramSettingsManager(dbManager);
    }


    public int getDefaultUser() {
        return dbProgramSettingsManager.getDefaultUser();
    }

    public void setDefaultUser(int userID) {
        dbProgramSettingsManager.setDefaultUser(userID);
        log.info("DefaultUsername is set.");
    }

    public void removeDefaultUser() {
        dbProgramSettingsManager.clearDefaultUser();
        log.info("DefaultUsername is cleared.");
    }

    /*public void setMainDirectoryVersion(final int version) { // TODO All the versioning needs to be done
        if (mainDirectoryVersionAlreadyChanged)
            log.info("Already changed main directory version this run, no further change needed.");
        else {
            settingsFile.setMainDirectoryVersion(version);
            // Current User should always be up to date, so its version can be updated with the Main Directory Version.
            if (!ClassHandler.mainRun().firstRun)
                ClassHandler.userInfoController().getUserSettings().setUserDirectoryVersion(version);
            saveSettingsFile();
            log.info("Main + User directory version updated to: " + version);
            mainDirectoryVersionAlreadyChanged = true;
        }
    }*/
}
