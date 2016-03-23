package com.maddogten.mtrack.util;

import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.gui.MultiChoice;
import com.maddogten.mtrack.gui.TextBox;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FirstRun {
    private final Logger log = Logger.getLogger(FirstRun.class.getName());

    public boolean programFirstRun() {
        log.info("First Run, Generating Files...");
        ClassHandler.mainRun().getLanguage();
        if (ClassHandler.mainRun().continueStarting) {
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
                fileManager.createFolder(Variables.LogsFolder);
            } else if (answer.getValue().matches(Strings.WithTheJar.getValue())) {
                Variables.setDataFolder(jarLocation);
                fileManager.createFolder(Variables.DirectoriesFolder);
                fileManager.createFolder(Variables.UsersFolder);
                fileManager.createFolder(Variables.LogsFolder);
            } else return false;
            if (Variables.enableFileLogging) {
                try {
                    GenericMethods.initFileLogging(log);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            boolean hasImportedFiles = false;
            if (new ConfirmBox().confirm(Strings.DoYouWantToImportFiles, null)) {
                if (fileManager.importSettings(true, null)) {
                    ClassHandler.programSettingsController().loadProgramSettingsFile();
                    if (ClassHandler.programSettingsController().getSettingsFile() != null) {
                        return true;
                    } else hasImportedFiles = true;
                }
            }
            generateProgramSettingsFile();
            ClassHandler.programSettingsController().loadProgramSettingsFile();
            if (Variables.makeLanguageDefault)
                ClassHandler.programSettingsController().setDefaultLanguage(Variables.language);
            boolean directoriesAlreadyAdded = hasImportedFiles && !ClassHandler.directoryController().findDirectories(true, false).isEmpty();
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
            boolean usersAlreadyAdd = hasImportedFiles && !ClassHandler.userInfoController().getAllUsers().isEmpty();
            if (usersAlreadyAdd) Strings.UserName.setValue(ClassHandler.mainRun().getUser());
            else
                Strings.UserName.setValue(textBox.addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, new ArrayList<>(), null));
            if (Strings.UserName.getValue().isEmpty()) ClassHandler.mainRun().continueStarting = false;
            else {
                if (!directoriesAlreadyAdded) {
                    try {
                        generateShowFilesThread.join();
                    } catch (InterruptedException e) {
                        GenericMethods.printStackTrace(log, e, this.getClass());
                    }
                }
                log.info(Strings.UserName.getValue());
                ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true));
                if (!usersAlreadyAdd) generateUserSettingsFile(Strings.UserName.getValue());
                ClassHandler.userInfoController().loadUserInfo();
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
        ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false);
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
        for (String aShow : ClassHandler.showInfoController().getShowsList()) {
            if (Variables.genUserShowInfoAtFirstFound)
                showSettings.put(aShow, new UserShowSettings(aShow, ClassHandler.showInfoController().findLowestSeason(aShow), ClassHandler.showInfoController().findLowestEpisode(ClassHandler.showInfoController().getEpisodesList(aShow, ClassHandler.showInfoController().findLowestSeason(aShow)))));
            else showSettings.put(aShow, new UserShowSettings(aShow, 1, 1));
        }
        new FileManager().save(new UserSettings(userName, showSettings, true, new String[0], ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, userName, Variables.UserFileExtension, false);
    }

    // During the firstRun, This is ran which shows a popup to add directory to scan. You can exit this without entering anything. If you do enter one, it will then ask you if you want to add another, or move on.
    private void addDirectories() {
        boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        int index = 0;
        while (addAnother) {
            boolean[] matched = ClassHandler.directoryController().addDirectory(index, textBox.addDirectory(Strings.PleaseEnterShowsDirectory, ClassHandler.directoryController().findDirectories(true, false), null));
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
            if (ClassHandler.userInfoController().getAllUsers().isEmpty()) ignoredShows = new ArrayList<>();
            else ignoredShows = ClassHandler.userInfoController().getIgnoredShows();
            findShows.findShows(directory.getDirectory()).forEach(aShow -> {
                log.info("Currently Processing: " + aShow);
                Map<Integer, Season> seasons = new HashMap<>();
                findShows.findSeasons(directory.getDirectory(), aShow).forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    Map<Integer, Episode> episodes = new HashMap<>();
                    ArrayList<String> episodesFull = findShows.findEpisodes(directory.getDirectory(), aShow, aSeason);
                    episodesFull.forEach(aEpisode -> {
                        log.info("Episode: " + aEpisode);
                        int[] episode = ClassHandler.showInfoController().getEpisodeInfo(aEpisode);
                        if (episode[0] != -2) {
                            episodes.put(episode[0], new Episode(episode[0], (directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), false));
                            if (episode[1] != -2)
                                episodes.put(episode[1], new Episode(episode[1], (directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), true));
                        }
                    });
                    if (!episodes.isEmpty()) seasons.put(aSeason, new Season(aSeason, episodes));
                });
                if (!seasons.isEmpty()) {
                    shows.put(aShow, new Show(aShow, seasons));
                    if (ignoredShows.contains(aShow)) ClassHandler.userInfoController().setIgnoredStatus(aShow, false);
                }
            });
            directory.setShows(shows);
            ClassHandler.directoryController().saveDirectory(directory, false);
            ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
        }
    }
}
