package program.io;

import program.information.ShowInfoController;
import program.util.Strings;
import program.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class GenerateSettingsFiles {
    private final Logger log = Logger.getLogger(GenerateSettingsFiles.class.getName());

    public void generateProgramSettingsFile() {
        FileManager fileManager = new FileManager();
        if (!fileManager.checkFileExists(Strings.EmptyString, program.util.Strings.SettingsFileName, Variables.SettingsExtension)) {
            log.info("Generating program settings file...");
            HashMap<String, ArrayList<String>> settingsFile = new HashMap<>();
            ArrayList<String> temp;

            // --Current Program Versions-- \\
            temp = new ArrayList<>();
            // Index 0 : Program Settings File Version
            temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
            // Index 1 : Main Directory Version
            temp.add(1, "0");
            settingsFile.put("ProgramVersions", temp);

            // --General Settings-- \\
            temp = new ArrayList<>();
            // Index 0 : Update Speed
            temp.add(0, "120");
            // Index 1 : show0Remaining
            temp.add(1, "false");
            // Index 2 : language
            temp.add(2, Strings.EmptyString);
            settingsFile.put("General", temp);

            // --Default User-- \\
            temp = new ArrayList<>();
            // Index 0 : Using default user
            temp.add(0, "false");
            // Index 1 : If using default user, What the username is.
            temp.add(1, Strings.EmptyString);
            settingsFile.put("DefaultUser", temp);

            // --Directories-- \\
            temp = new ArrayList<>();
            settingsFile.put("Directories", temp);

            fileManager.save(settingsFile, Strings.EmptyString, program.util.Strings.SettingsFileName, Variables.SettingsExtension, true);
        }
    }

    public void generateUserSettingsFile(String userName) {
        FileManager fileManager = new FileManager();
        if (!fileManager.checkFileExists(Variables.SettingsExtension, userName, ".settings")) {
            log.info("Generating settings file for " + userName + "...");
            HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile = new HashMap<>();
            HashMap<String, HashMap<String, String>> tempPut;
            HashMap<String, String> temp;

            // ~~User Settings~~ \\
            tempPut = new HashMap<>();
            // --Current User Versions-- \\
            temp = new HashMap<>();
            // Index 0 : User Settings File Version
            temp.put("0", String.valueOf(Variables.UserSettingsFileVersion));
            // Index 1 : User Directory Version
            temp.put("1", "1");
            tempPut.put("UserVersions", temp);

            userSettingsFile.put("UserSettings", tempPut);

            // isActive - "isActive"
            // isIgnored - "isIgnored
            // Current Season - "CurrentSeason"
            // Current Episode - "CurrentEpisode"

            tempPut = new HashMap<>();
            ArrayList<String> showsList = ShowInfoController.getShowsList();
            for (String aShow : showsList) {
                    temp = new HashMap<>();
                    temp.put("isActive", "false");
                    temp.put("isIgnored", "false");
                    temp.put("isHidden", "false");
                temp.put("CurrentSeason", String.valueOf(ShowInfoController.findLowestSeason(aShow)));
                Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Integer.parseInt(temp.get("CurrentSeason")));
                temp.put("CurrentEpisode", ShowInfoController.findLowestEpisode(episodes));
                tempPut.put(aShow, temp);
                }

            userSettingsFile.put("ShowSettings", tempPut);
            fileManager.save(userSettingsFile, Variables.UsersFolder, userName, Variables.UsersExtension, false);
        }
    }
}
