package program.io;

import program.information.ShowInfoController;
import program.util.Variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class GenerateSettingsFiles {

    public static void generateProgramSettingsFile(String settingsFileName, String settingsFolder, String extension, Boolean override) {
        if (override || !FileManager.checkFileExists(settingsFolder, settingsFileName, extension)) {
            System.out.println("Generating program settings file...\n");
            HashMap<String, ArrayList<String>> settingsFile = new HashMap<>();

            ArrayList<String> temp = new ArrayList<>();

            // --Default User-- \\
            // Index 0 : Using default user
            // Index 1 : If using default user, What is the username.
            temp.add(0, "false");
            temp.add(1, "");
            settingsFile.put("DefaultUser", temp);

            // --Directories-- \\
            temp = new ArrayList<>();
            settingsFile.put("Directories", temp);

            FileManager.save(settingsFile, settingsFolder, settingsFileName, extension, true);
        }
    }

    public static void generateUserSettingsFile(String userName, String settingsFolder, String extension, Boolean override) {
        if (override || !FileManager.checkFileExists(settingsFolder, userName, ".settings")) {
            System.out.println("Generating settings file for " + userName + "...\n");
            HashMap<String, HashMap<String, String[]>> userSettingsFile = new HashMap<>();
            HashMap<String, String[]> tempPut = new HashMap<>();
            String[] Temp;

            tempPut = new HashMap<>();

            // Temp[0]- Active
            // Temp[1]- hidden
            // Temp[2]- Total Number of Seasons
            // Temp[3]- Current Season
            // Temp[4]- Highest Season
            // Temp[5]- Total Number of Episodes in Current Season
            // Temp[6]- Current Episode

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
                Temp[5] = String.valueOf(ShowInfoController.getHighestEpisode(aShow, episodes));
                Temp[6] = String.valueOf(ShowInfoController.getLowestEpisode(aShow, episodes, Integer.parseInt(Temp[5])));
                System.out.println(aShow);
                System.out.println(aShow + " - " + Arrays.toString(Temp));
                tempPut.put(String.valueOf(aShow), Temp);
            }
            userSettingsFile.put("ShowSettings", tempPut);
            FileManager.save(userSettingsFile, Variables.settingsFolder, userName, Variables.settingsExtension, false);
        }
    }
}
