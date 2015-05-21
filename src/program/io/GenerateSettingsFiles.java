package program.io;

import program.information.ShowInfoController;
import program.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class GenerateSettingsFiles {
    private final Logger log = Logger.getLogger(GenerateSettingsFiles.class.getName());

    public void generateProgramSettingsFile(String settingsFileName, String settingsFolder, String extension, Boolean override) {
        FileManager fileManager = new FileManager();
        if (override || !fileManager.checkFileExists(settingsFolder, settingsFileName, extension)) {
            log.info("GenerateSettingsFiles- Generating program settings file...\n");
            HashMap<String, ArrayList<String>> settingsFile = new HashMap<>();
            ArrayList<String> temp;

            // --Current Program Versions-- \\
            temp = new ArrayList<>();
            temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
            settingsFile.put("ProgramVersions", temp);

            // --General Settings-- \\
            temp = new ArrayList<>();
            // Index 0 : Update Speed
            temp.add(0, "120");
            settingsFile.put("General", temp);

            // --Default User-- \\
            temp = new ArrayList<>();
            // Index 0 : Using default user
            temp.add(0, "false");
            // Index 1 : If using default user, What the username is.
            temp.add(1, Variables.EmptyString);
            settingsFile.put("DefaultUser", temp);

            // --Directories-- \\
            temp = new ArrayList<>();
            settingsFile.put("Directories", temp);

            fileManager.save(settingsFile, settingsFolder, settingsFileName, extension, true);
        }
    }

    public void generateUserSettingsFile(String userName, String settingsFolder, Boolean override) {
        FileManager fileManager = new FileManager();
        if (override || !fileManager.checkFileExists(settingsFolder, userName, ".settings")) {
            log.info("GenerateSettingsFiles- Generating settings file for " + userName + "...\n");
            HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile = new HashMap<>();
            HashMap<String, HashMap<String, String>> tempPut;
            HashMap<String, String> temp;

            // ~~User Settings~~ \\
            tempPut = new HashMap<>();
            // --Current User Versions-- \\
            temp = new HashMap<>();
            temp.put("0", String.valueOf(Variables.UserSettingsFileVersion));
            tempPut.put("UserVersions", temp);

            userSettingsFile.put("UserSettings", tempPut);

            // isActive - "isActive"
            // isIgnored - "isIgnored
            // Current Season - "CurrentSeason"
            // Current Episode - "CurrentEpisode"

            tempPut = new HashMap<>();
            Object[] showsList = ShowInfoController.getShowsList();
            if (showsList != null) {
                for (Object aShow : showsList) {
                    temp = new HashMap<>();
                    temp.put("isActive", "false");
                    temp.put("isIgnored", "false");
                    temp.put("isHidden", "false");
                    temp.put("CurrentSeason", String.valueOf(ShowInfoController.findLowestSeason(String.valueOf(aShow))));
                    Set<String> episodes = ShowInfoController.getEpisodesList(String.valueOf(aShow), Integer.parseInt(temp.get("CurrentSeason")));
                    temp.put("CurrentEpisode", String.valueOf(ShowInfoController.findLowestEpisode(episodes)));
                    tempPut.put(String.valueOf(aShow), temp);
                }
            }
            userSettingsFile.put("ShowSettings", tempPut);
            fileManager.save(userSettingsFile, Variables.UsersFolder, userName, Variables.UsersExtension, false);
        }
    }
}
