package program.io;

import program.information.ShowInfoController;
import program.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class GenerateSettingsFiles {
    private static final Logger log = Logger.getLogger(GenerateSettingsFiles.class.getName());

    public static void generateProgramSettingsFile(String settingsFileName, String settingsFolder, String extension, Boolean override) {
        if (override || !FileManager.checkFileExists(settingsFolder, settingsFileName, extension)) {
            log.info("GenerateSettingsFiles- Generating program settings file...\n");
            HashMap<String, ArrayList<String>> settingsFile = new HashMap<>();
            ArrayList<String> temp;

            // --General Settings-- \\
            temp = new ArrayList<>();
            // Index 0 : Update Speed
            temp.add(0, "120");
            settingsFile.put("General", temp);

            // --Default User-- \\
            temp = new ArrayList<>();
            // Index 0 : Using default user
            // Index 1 : If using default user, What is the username.
            temp.add(0, "false");
            temp.add(1, Variables.EmptyString);
            settingsFile.put("DefaultUser", temp);

            // --Directories-- \\
            temp = new ArrayList<>();
            settingsFile.put("Directories", temp);

            FileManager.save(settingsFile, settingsFolder, settingsFileName, extension, true);
        }
    }

    public static void generateUserSettingsFile(String userName, String settingsFolder, Boolean override) {
        if (override || !FileManager.checkFileExists(settingsFolder, userName, ".settings")) {
            log.info("GenerateSettingsFiles- Generating settings file for " + userName + "...\n");
            HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile = new HashMap<>();
            HashMap<String, HashMap<String, String>> tempPut = new HashMap<>();

            // isActive - "isActive"
            // isHidden - "isHidden
            // Current Season - "CurrentSeason"
            // Current Episode - "CurrentEpisode"

            HashMap<String, String> temp;
            Object[] showsList = ShowInfoController.getShowsList();
            for (Object aShow : showsList) {
                temp = new HashMap<>();
                temp.put("isActive", "false");
                temp.put("isHidden", "false");
                temp.put("CurrentSeason", String.valueOf(ShowInfoController.getLowestSeason(String.valueOf(aShow), ShowInfoController.getHighestSeason(String.valueOf(aShow)))));
                Set<String> episodes = ShowInfoController.getEpisodesList(String.valueOf(aShow), temp.get("CurrentSeason"));
                temp.put("CurrentEpisode", String.valueOf(ShowInfoController.getLowestEpisode(episodes, ShowInfoController.getHighestEpisode(episodes))));
                tempPut.put(String.valueOf(aShow), temp);
            }
            userSettingsFile.put("ShowSettings", tempPut);
            FileManager.save(userSettingsFile, Variables.UsersFolder, userName, Variables.UsersExtension, false);
        }
    }
}
