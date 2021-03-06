package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.*;
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

    public boolean programFirstRun() throws IOException {
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
            File appData = OperatingSystem.programFolder;
            StringProperty answer = new MultiChoice().multipleButtons(new StringProperty[]{Strings.WhereWouldYouLikeTheProgramFilesToBeStored, Strings.HoverOverAButtonForThePath}, new StringProperty[]{Strings.InAppData, Strings.WithTheJar}, new StringProperty[]{new SimpleStringProperty(appData.toString()), new SimpleStringProperty(jarLocation.toString())}, null);
            if (answer.getValue().matches(Strings.InAppData.getValue())) {
                Variables.setDataFolder(appData);
                this.createFolders(true, fileManager);
            } else if (answer.getValue().matches(Strings.WithTheJar.getValue())) {
                Variables.setDataFolder(jarLocation);
                this.createFolders(false, fileManager);
            } else return false;
            if (Variables.enableFileLogging) {
                try {
                    GenericMethods.initFileLogging(log);
                } catch (IOException e) {
                    GenericMethods.printStackTrace(log, e, this.getClass());
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
            boolean addDirectories = !hasImportedFiles || ClassHandler.directoryController().findDirectories(true, false, true).isEmpty();
            Thread generateShowFilesThread = null;
            if (addDirectories) {
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
                if (addDirectories) {
                    LoadingBox loadingBox = new LoadingBox();
                    if (generateShowFilesThread.isAlive()) loadingBox.loadingBox(generateShowFilesThread);
                }
                log.info(Strings.UserName.getValue());
                ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, false));
                if (!usersAlreadyAdd) generateUserSettingsFile(Strings.UserName.getValue());
                ClassHandler.userInfoController().loadUserInfo();
            }

        }
        return false;
    }

    private void createFolders(final boolean createBaseFolder, final FileManager fileManager) {
        if (createBaseFolder) fileManager.createFolder("");
        fileManager.createFolder(Variables.DirectoriesFolder);
        fileManager.createFolder(Variables.UsersFolder);
        fileManager.createFolder(Variables.LogsFolder);
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
        ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false, true);
        directories.forEach(aDirectory -> {
            log.info("Currently generating show file for: " + aDirectory.getDirectory());
            generateShowsFile(aDirectory);
        });
        log.info("Finished generating show files.");
    }

    // Generates a user settings file for the given username.
    public void generateUserSettingsFile(final String userName) throws IOException {
        log.info("Attempting to generate settings file for " + userName + '.');
        Map<String, UserShowSettings> showSettings = new HashMap<>();
        for (String aShow : ClassHandler.showInfoController().getShowsList()) {
            if (Variables.genUserShowInfoAtFirstFound)
                showSettings.put(aShow, new UserShowSettings(aShow, ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getSeasonsList(aShow)), ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getEpisodesList(aShow, ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getSeasonsList(aShow))))));
            else
                showSettings.put(aShow, new UserShowSettings(aShow, ClassHandler.showInfoController().getEpisode(aShow, 1, 0) != null ? 0 : 1, 1));
        }
        new FileManager().save(new UserSettings(userName, showSettings, true, new VideoPlayerSelectorBox().videoPlayerSelector(null), new String[0], new HashMap<>(), ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, userName, Variables.UserFileExtension, false);
    }

    // During the firstRun, This is ran which shows a popup to add directory to scan. You can exit this without entering anything. If you do enter one, it will then ask you if you want to add another, or move on.
    private void addDirectories() {
        TextBox textBox = new TextBox();
        ArrayList<File> directories = textBox.addDirectory(Strings.PleaseEnterShowsDirectory, ClassHandler.directoryController().findDirectories(true, false, true), null);
        directories.forEach(file -> {
            Long[] matched = ClassHandler.directoryController().addDirectory(file);
            if (matched[0] == null && matched[1] == null)
                new MessageBox(new StringProperty[]{Strings.DirectoryWasADuplicate, new SimpleStringProperty(file.toString())}, null);
        });
    }

    // This generates a new showsFile for the given folder, then saves it as "Directory-[index].[ShowFileExtension].
    public void generateShowsFile(final Directory directory) {
        FileManager fileManager = new FileManager();
        String fileName = "";
        if (!fileManager.checkFileExists(Variables.DirectoriesFolder, fileName, Variables.ShowFileExtension)) {
            log.info("Generating ShowsFile for: " + directory.getDirectory());
            FindShows findShows = new FindShows();
            Map<String, Show> shows = new HashMap<>();
            final ArrayList<String> ignoredShows;
            if (!Main.programFullyRunning || ClassHandler.userInfoController().getAllUsers().isEmpty())
                ignoredShows = new ArrayList<>();
            else ignoredShows = ClassHandler.userInfoController().getIgnoredShows();
            findShows.findShows(directory.getDirectory()).forEach(aShow -> {
                log.info("Currently Processing: " + aShow);
                Map<Integer, Season> seasons = new HashMap<>();
                findShows.findSeasons(directory.getDirectory(), aShow).forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    String seasonFolderName = GenericMethods.getSeasonFolderName(directory.getDirectory(), aShow, aSeason);
                    Map<Integer, Episode> episodes = new HashMap<>();
                    ArrayList<String> episodesFull = findShows.findEpisodes(directory.getDirectory(), aShow, aSeason);
                    episodesFull.forEach(aEpisode -> {
                        log.info("Episode: " + aEpisode);
                        int[] episode = ClassHandler.showInfoController().getEpisodeInfo(aEpisode);
                        if (episode[0] != -2) {
                            episodes.put(episode[0], new Episode(episode[0], (directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + seasonFolderName + Strings.FileSeparator + aEpisode), false));
                            if (episode[1] != -2)
                                episodes.put(episode[1], new Episode(episode[1], (directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + seasonFolderName + Strings.FileSeparator + aEpisode), true));
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
            if (Main.programFullyRunning)
                ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
        }
    }
}
