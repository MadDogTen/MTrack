package com.maddogten.mtrack.util;

import com.maddogten.mtrack.MainRun;
import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.gui.MultiChoice;
import com.maddogten.mtrack.gui.TextBox;
import com.maddogten.mtrack.information.DirectoryController;
import com.maddogten.mtrack.information.ProgramSettingsController;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.information.settings.ProgramSettings;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.io.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FirstRun {
    private final Logger log = Logger.getLogger(FirstRun.class.getName());

    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;
    private final DirectoryController directoryController;
    private final MainRun mainRun;

    public FirstRun(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController, DirectoryController directoryController, MainRun mainRun) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
        this.directoryController = directoryController;
        this.mainRun = mainRun;
    }

    public boolean programFirstRun() {
        log.info("First Run, Generating Files...");
        mainRun.getLanguage();
        if (mainRun.continueStarting) {
            FileManager fileManager = new FileManager();
            File jarLocation = new File(Strings.EmptyString);
            try {
                jarLocation = fileManager.getJarLocationFolder();
            } catch (UnsupportedEncodingException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
            File appData = fileManager.findProgramFolder();
            StringProperty answer = new MultiChoice().multipleButtons(new StringProperty[]{Strings.WhereWouldYouLikeTheProgramFilesToBeStored, Strings.HoverOverAButtonForThePath}, new StringProperty[]{Strings.InAppData, Strings.WithTheJar}, new StringProperty[]{new SimpleStringProperty(appData.toString()), new SimpleStringProperty(jarLocation.toString())}, null);
            if (answer.getValue().matches(Strings.InAppData.getValue())) {
                Variables.setDataFolder(appData);
                fileManager.createFolder("");
                fileManager.createFolder(Variables.DirectoriesFolder);
                fileManager.createFolder(Variables.UsersFolder);
            } else if (answer.getValue().matches(Strings.WithTheJar.getValue())) {
                Variables.setDataFolder(jarLocation);
                fileManager.createFolder(Variables.DirectoriesFolder);
                fileManager.createFolder(Variables.UsersFolder);
            }
            boolean hasImportedFiles = false;
            if (new ConfirmBox().confirm(Strings.DoYouWantToImportFiles, null)) {
                if (fileManager.importSettings(true, null)) {
                    programSettingsController.loadProgramSettingsFile();
                    if (programSettingsController.getSettingsFile() != null) {
                        return true;
                    } else hasImportedFiles = true;
                }
            }
            generateProgramSettingsFile();
            programSettingsController.loadProgramSettingsFile();
            if (Variables.makeLanguageDefault) programSettingsController.setDefaultLanguage(Variables.language);
            boolean directoriesAlreadyAdded = hasImportedFiles && !directoryController.getDirectories().isEmpty();
            Thread generateShowFilesThread = null;
            if (!directoriesAlreadyAdded) {
                addDirectories();
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        generateShowFiles();
                        return null;
                    }
                };
                generateShowFilesThread = new Thread(task);
                generateShowFilesThread.start();
            }
            TextBox textBox = new TextBox();
            boolean usersAlreadyAdd = hasImportedFiles && !userInfoController.getAllUsers().isEmpty();
            if (usersAlreadyAdd) Strings.UserName.setValue(mainRun.getUser());
            else
                Strings.UserName.setValue(textBox.addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, new ArrayList<>(), null));
            if (Strings.UserName.getValue().isEmpty()) mainRun.continueStarting = false;
            else {
                if (!directoriesAlreadyAdded) {
                    try {
                        generateShowFilesThread.join();
                    } catch (InterruptedException e) {
                        GenericMethods.printStackTrace(log, e, this.getClass());
                    }
                }
                log.info(Strings.UserName.getValue());
                showInfoController.loadShowsFile();
                if (!usersAlreadyAdd) generateUserSettingsFile(Strings.UserName.getValue());
                userInfoController.loadUserInfo();
            }

        }
        return false;
    }

    // File Generators
    // Generates the program settings file.
    public void generateProgramSettingsFile() {
        log.info("Attempting to generate program settings file.");
        new FileManager().save(new ProgramSettings(), Strings.EmptyString, com.maddogten.mtrack.util.Strings.SettingsFileName, Variables.SettingFileExtension, true);
    }

    // Generates the ShowFiles (If a directory is added, otherwise this is skipped).
    private void generateShowFiles() {
        log.info("Generating show files for first run...");
        ArrayList<Directory> directories = directoryController.getDirectories();
        directories.forEach(aDirectory -> {
            log.info("Currently generating show file for: " + aDirectory.getDirectory());
            generateShowsFile(aDirectory);
        });
        log.info("Finished generating show files.");
    }

    // Generates a user settings file for the given username.
    public void generateUserSettingsFile(String userName) {
        log.info("Attempting to generate settings file for " + userName + '.');
        Map<String, UserShowSettings> showSettings = new HashMap<>();
        for (String aShow : showInfoController.getShowsList()) {
            if (Variables.genUserShowInfoAtFirstFound)
                showSettings.put(aShow, new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, showInfoController.findLowestSeason(aShow)))));
            else showSettings.put(aShow, new UserShowSettings(aShow, 1, 1));
        }
        new FileManager().save(new UserSettings(userName, showSettings, new String[0], programSettingsController.getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, userName, Variables.UserFileExtension, false);
    }

    // During the firstRun, This is ran which shows a popup to add directory to scan. You can exit this without entering anything. If you do enter one, it will then ask you if you want to add another, or move on.
    private void addDirectories() {
        boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        int index = 0;
        while (addAnother) {
            boolean[] matched = directoryController.addDirectory(index, textBox.addDirectory(Strings.PleaseEnterShowsDirectory, directoryController.getDirectories(), null));
            index++;
            if (!matched[0] && !matched[1])
                new MessageBox().message(new StringProperty[]{Strings.DirectoryWasADuplicate}, null);
            else if (matched[1]) break;
            if (!confirmBox.confirm(Strings.AddAnotherDirectory, null)) addAnother = false;
        }
    }

    // This generates a new showsFile for the given folder, then saves it as "Directory-[index].[ShowFileExtension].
    public void generateShowsFile(Directory directory) {
        FileManager fileManager = new FileManager();
        String fileName = "";
        if (!fileManager.checkFileExists(Variables.DirectoriesFolder, fileName, Variables.ShowFileExtension)) {
            log.info("Generating ShowsFile for: " + directory.getDirectory());
            FindShows findShows = new FindShows();
            Map<String, Show> shows = new HashMap<>();
            final ArrayList<String> ignoredShows;
            if (userInfoController.getAllUsers().isEmpty()) ignoredShows = new ArrayList<>();
            else ignoredShows = userInfoController.getIgnoredShows();
            findShows.findShows(directory.getDirectory()).forEach(aShow -> {
                log.info("Currently Processing: " + aShow);
                Map<Integer, Season> seasons = new HashMap<>();
                findShows.findSeasons(directory.getDirectory(), aShow).forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    Map<Integer, Episode> episodes = new HashMap<>();
                    ArrayList<String> episodesFull = findShows.findEpisodes(directory.getDirectory(), aShow, aSeason);
                    episodesFull.forEach(aEpisode -> {
                        log.info("Episode: " + aEpisode);
                        int[] episode = showInfoController.getEpisodeInfo(aEpisode);
                        if (episode != null && episode.length > 0) {
                            episodes.put(episode[0], new Episode(episode[0], (directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), false));
                            if (episode.length == 2) {
                                episodes.put(episode[1], new Episode(episode[1], (directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), true));
                            }
                        }
                    });
                    if (!episodes.isEmpty()) seasons.put(aSeason, new Season(aSeason, episodes));
                });
                if (!seasons.isEmpty()) {
                    shows.put(aShow, new Show(aShow, seasons));
                    if (ignoredShows.contains(aShow)) userInfoController.setIgnoredStatus(aShow, false);
                }
            });
            directory.setShows(shows);
            directoryController.saveDirectory(directory, false);
            programSettingsController.setMainDirectoryVersion(programSettingsController.getSettingsFile().getMainDirectoryVersion() + 1);
        }
    }
}
