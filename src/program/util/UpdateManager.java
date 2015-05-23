package program.util;

import program.information.ChangeReporter;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class UpdateManager {
    private static final Logger log = Logger.getLogger(UpdateManager.class.getName());
    private final InnerVersionChecker checker = new InnerVersionChecker();

    public void updateFiles() {
        log.info("Checking if inner versions are matched...");
        int version = checker.checkProgramSettingsFileVersion();
        if (version != -1) {
            log.info("Program settings file versions didn't match, Updating...");
            convertProgramSettingsFile(version, Variables.ProgramSettingsFileVersion);
        } else log.info("Program settings file versions matched.");
        version = checker.checkUserSettingsFileVersion();
        if (version != -1) {
            log.info("User settings file versions didn't match, Updating...");
            convertUserSettingsFile(version, Variables.UserSettingsFileVersion);
        } else log.info("User settings file versions matched.");
        version = ProgramSettingsController.getMainDirectoryVersion();
        if (version != UserInfoController.getUserDirectoryVersion()) {
            log.info("User directory version didn't match, Updating...");
            updateUserShows(version);
        } else log.info("User directory version matched.");
        log.info("Finished checking if inner versions are matched.");
    }

    private void convertProgramSettingsFile(int oldVersion, int newVersion) {
        HashMap<String, ArrayList<String>> programSettingsFile = ProgramSettingsController.getSettingsFile();
        Boolean updated = false;
        switch (oldVersion) {
            case -2:
                ArrayList<String> temp = new ArrayList<>();
                temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
                programSettingsFile.put("ProgramVersions", temp);
                log.info("Program settings file has been updated from version -2.");
            case 1:
                programSettingsFile.get("General").add(1, Variables.dataFolder);
                log.info("Program has been updated from version 1.");
            case 2:
                log.info("Program has been updated from version 2.");
            case 1001:
                programSettingsFile.get("General").remove(1);
                log.info("Program has been updated from version 1001.");
            case 1002:
                log.info("Converting directories...");
                if (programSettingsFile.containsKey("Directories")) {
                    ArrayList<String> directories = programSettingsFile.get("Directories");
                    ArrayList<String> directoriesFixed = new ArrayList<>();
                    for (String aDirectory : directories) {
                        int index = directories.indexOf(aDirectory);
                        directoriesFixed.add(index + ">" + aDirectory);
                        log.info("Converted " + aDirectory + " to " + index + '>' + aDirectory);
                    }
                    programSettingsFile.replace("Directories", directoriesFixed);
                } else log.info("Program settings file contains no directories, Nothing was converted.");
                log.info("Finished converting directories");
                log.info("Program has been updated from version 1002.");
            case 1003:
                programSettingsFile.get("ProgramVersions").add(1, "1");
                log.info("Program has been updated from version 1003.");
                updated = true;
        }

        if (updated) {
            // Update Program Settings File Version
            programSettingsFile.get("ProgramVersions").set(0, String.valueOf(newVersion));

            ProgramSettingsController.setSettingsFile(programSettingsFile);
            ProgramSettingsController.saveSettingsFile();
            log.info("Program settings file was successfully updated to version " + newVersion + '.' );
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
                log.info("User settings file has been updated from version -2.");
            case 1:
                HashMap<String, HashMap<String, String>> shows = userSettingsFile.get("ShowSettings");
                for (String aShow : shows.keySet()) {
                    shows.get(aShow).put("isHidden", "false");
                }
                log.info("User settings file has been updated from version 1.");
            case 2:
                Object[] allShows = ShowInfoController.getShowsList();
                if (allShows != null) {
                    for (Object aShow : allShows) {
                        if (!userSettingsFile.get("ShowSettings").keySet().contains(String.valueOf(aShow))) {
                            log.info("Adding " + aShow + " to user settings file.");
                            HashMap<String, String> temp2 = new HashMap<>();
                            temp2.put("isActive", "false");
                            temp2.put("isIgnored", "false");
                            temp2.put("isHidden", "false");
                            temp2.put("CurrentSeason", String.valueOf(ShowInfoController.findLowestSeason(String.valueOf(aShow))));
                            Set<String> episodes = ShowInfoController.getEpisodesList(String.valueOf(aShow), Integer.parseInt(temp2.get("CurrentSeason")));
                            temp2.put("CurrentEpisode", String.valueOf(ShowInfoController.findLowestEpisode(episodes)));
                            userSettingsFile.get("ShowSettings").put(String.valueOf(aShow), temp2);
                        }
                    }
                }
                log.info("User settings file has been updated from version 2.");
            case 1000:
                if (!userSettingsFile.containsKey("ShowSettings")) {
                    userSettingsFile.put("ShowSettings", new HashMap<>());
                }
                userSettingsFile.get("UserSettings").get("UserVersions").put("1", "0");
                log.info("User settings file has been updated from version 1000.");
                updated = true;
        }

        if (updated) {
            // Update User Settings File Version
            userSettingsFile.get("UserSettings").get("UserVersions").replace("0", String.valueOf(newVersion));

            UserInfoController.setUserSettingsFile(userSettingsFile);
            UserInfoController.saveUserSettingsFile();
            log.info("User settings file was successfully updated to version " + newVersion + '.' );
        } else log.info("User settings file was not updated. This is an error, please report.");
    }

    private void updateUserShows(int newVersion) {
        ArrayList<String> shows = ShowInfoController.getShowsArrayList();
        ArrayList<String> userShows = UserInfoController.getAllNonIgnoredShows();
        ArrayList<String> ignoredShows = UserInfoController.getIgnoredShows();
        Boolean changed = false;
        for (String aShow : shows) {
            if (!userShows.contains(aShow) && !ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and added.");
                ChangeReporter.addChange(aShow + " has changed");
                UserInfoController.addNewShow(aShow);
                changed = true;
            } else if (ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and un-ignored.");
                ChangeReporter.addChange(aShow + " has changed");
                UserInfoController.setIgnoredStatus(aShow, false);
                changed = true;
            }
        }
        for (String aShow : userShows) {
            if (!shows.contains(aShow)) {
                log.info(aShow + " wasn't found during user shows update.");
                ChangeReporter.addChange(aShow + " has changed");
                UserInfoController.setIgnoredStatus(aShow, true);
                changed = true;
            }
        }
        if (!changed) {
            log.info("No changes found.");
        }
        UserInfoController.setUserDirectoryVersion(newVersion);
    }
}
