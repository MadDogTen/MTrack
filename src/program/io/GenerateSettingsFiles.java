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

            ArrayList<String> temp = new ArrayList<>();

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
            HashMap<String, HashMap<String, String[]>> userSettingsFile = new HashMap<>();
            HashMap<String, String[]> tempPut = new HashMap<>();

            // Temp[0]- Active
            // Temp[1]- hidden
            // Temp[2]- Total Number of Seasons
            // Temp[3]- Current Season
            // Temp[4]- Highest Season
            // Temp[5]- Total Number of Episodes in Current Season
            // Temp[6]- Current Episode

            String[] Temp;
            Object[] showsList = ShowInfoController.getShowsList();
            for (Object aShow : showsList) {
                Temp = new String[7];
                Temp[0] = "true";
                Temp[1] = "false";
                Object[] Temp2 = ShowInfoController.getSeasonsListObject(aShow);
                if (Temp2 != null) {
                    Temp[2] = String.valueOf(Temp2.length);
                } else Temp[2] = "-1";
                Temp[3] = String.valueOf(ShowInfoController.getLowestSeason(aShow, ShowInfoController.getHighestSeason(aShow)));
                Temp[4] = String.valueOf(ShowInfoController.getHighestSeason(aShow));
                Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Temp[3]);
                Temp[5] = String.valueOf(ShowInfoController.getHighestEpisode(episodes));
                Temp[6] = String.valueOf(ShowInfoController.getLowestEpisode(episodes, Integer.parseInt(Temp[5])));
                tempPut.put(String.valueOf(aShow), Temp);
            }
            userSettingsFile.put("ShowSettings", tempPut);
            FileManager.save(userSettingsFile, Variables.UsersFolder, userName, Variables.UsersExtension, false);
        }
    }
}
