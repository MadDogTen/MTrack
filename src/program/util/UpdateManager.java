package program.util;

import program.information.ProgramSettingsController;
import program.information.UserInfoController;
import program.io.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class UpdateManager {
    private static final Logger log = Logger.getLogger(UpdateManager.class.getName());
    InnerVersionChecker checker = new InnerVersionChecker();

    public void updateFiles() {
        log.info("Checking if Inner Versions are matched...");
        int version = checker.checkProgramSettingsFileVersion();
        if (version != -1) {
            log.info("Program settings file versions didn't match, Updating...");
            convertProgramSettingsFile(version, Variables.ProgramSettingsFileVersion);
        } else log.info("Program settings file versions matched.");
        version = checker.checkUserSettingsFileVersion();
        if (version != -1) {
            log.info("User settings file versions didn't match, Updating...");
            convertUserSettingsFile(version, Variables.UserSettingsFileVersion);
        }
        log.info("Program settings file versions matched.");
        log.info("Finished if Inner Versions are matched...");
    }

    private void convertProgramSettingsFile(int oldVersion, int newVersion) {
        HashMap<String, ArrayList<String>> programSettingsFile = ProgramSettingsController.getSettingsFile();
        Boolean updated = false;
        switch (oldVersion) {
            case -2:
                ArrayList<String> temp = new ArrayList<>();
                temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
                programSettingsFile.put("ProgramVersions", temp);
            case 1: //TODO Add this to case -2
                programSettingsFile.get("General").add(1, new FileManager().getDataFolder());
                updated = true;
        }

        if (updated) {
            // Update Program Settings File Version
            programSettingsFile.get("ProgramVersions").set(0, String.valueOf(newVersion));

            ProgramSettingsController.setSettingsFile(programSettingsFile);
            ProgramSettingsController.saveSettingsFile();
            log.info("Program settings file was successfully updated.");
        } else log.info("Program settings file was not updated. This is an error, please report.");
    }

    private void convertUserSettingsFile(int oldVersion, int newVersion) {
        HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile = UserInfoController.getUserSettingsFile();
        Boolean updated = false;
        switch (oldVersion) {
            case -2:
                HashMap<String, HashMap<String, String>> tempPut;
                HashMap<String, String> temp;
                tempPut = new HashMap<>();
                temp = new HashMap<>();
                temp.put("0", String.valueOf(Variables.UserSettingsFileVersion));
                tempPut.put("UserVersions", temp);
                userSettingsFile.put("UserSettings", tempPut);
                updated = true;
        }

        if (updated) {
            // Update User Settings File Version
            userSettingsFile.get("UserSettings").get("UserVersions").replace("0", String.valueOf(newVersion));

            UserInfoController.setUserSettingsFile(userSettingsFile);
            UserInfoController.saveUserSettingsFile();
            log.info("User settings file was successfully updated.");
        } else log.info("User settings file was not updated. This is an error, please report.");
    }
}
