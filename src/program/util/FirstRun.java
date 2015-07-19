package program.util;

import javafx.concurrent.Task;
import program.Main;
import program.gui.ConfirmBox;
import program.gui.DualChoiceButtons;
import program.gui.MessageBox;
import program.gui.TextBox;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.information.settings.ProgramSettings;
import program.information.settings.UserSettings;
import program.information.settings.UserShowSettings;
import program.information.show.Episode;
import program.information.show.Season;
import program.information.show.Show;
import program.io.FileManager;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FirstRun {
    private final Logger log = Logger.getLogger(FirstRun.class.getName());

    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;

    public FirstRun(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
    }

    public void programFirstRun() {
        log.info("First Run, Generating Files...");
        Main.getMainRun().getLanguage();
        FileManager fileManager = new FileManager();
        File jarLocation = null;
        try {
            jarLocation = fileManager.getJarLocationFolder();
        } catch (UnsupportedEncodingException e) {
            log.severe(Arrays.toString(e.getStackTrace()));
        }
        File appData = fileManager.getAppDataFolder();
        String answer = new DualChoiceButtons().display(Strings.WhereWouldYouLikeTheProgramFilesToBeStored, Strings.HoverOverAButtonForThePath, Strings.InAppData, Strings.WithTheJar, String.valueOf(appData), String.valueOf(jarLocation), null);
        if (answer.matches(Strings.InAppData)) {
            Variables.setDataFolder(appData);
        } else if (answer.matches(Strings.WithTheJar)) {
            Variables.setDataFolder(jarLocation);
        }
        fileManager.createFolder(Strings.EmptyString);
        generateProgramSettingsFile();
        programSettingsController.loadProgramSettingsFile();
        programSettingsController.setLanguage(Variables.language);
        addDirectories();

        final boolean[] taskRunning = {true};
        Task<Void> task = new Task<Void>() {
            @SuppressWarnings("ReturnOfNull")
            @Override
            protected Void call() throws Exception {
                generateShowFiles();
                taskRunning[0] = false;
                return null;
            }
        };
        new Thread(task).start();
        TextBox textBox = new TextBox();
        Strings.UserName = textBox.display(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, new ArrayList<>(), null);
        while (taskRunning[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
        log.info(Strings.UserName);
        showInfoController.loadShowsFile();
        generateUserSettingsFile(Strings.UserName);
        userInfoController.loadUserInfo();
    }

    // File Generators
    // Generates the program settings file.
    public void generateProgramSettingsFile() {
        log.info("Attempting to generate program settings file.");
        new FileManager().save(new ProgramSettings(), Strings.EmptyString, program.util.Strings.SettingsFileName, Variables.SettingsExtension, true);
    }

    // Generates the ShowFiles (If a directory is added, otherwise this is skipped).
    private void generateShowFiles() {
        log.info("Generating show files for first run...");
        ArrayList<String> directories = programSettingsController.getDirectories();
        directories.forEach(aDirectory -> {
            log.info("Currently generating show files for: " + aDirectory);
            int fileName = directories.indexOf(aDirectory);
            File file = new File(aDirectory);
            generateShowsFile(fileName, file);
        });
        log.info("Finished generating show files.");
    }

    // Generates a user settings file for the given username.
    private void generateUserSettingsFile(String userName) {
        log.info("Attempting to generate settings file for " + userName + '.');
        Map<String, UserShowSettings> showSettings = new HashMap<>();
        ArrayList<String> showsList = showInfoController.getShowsList();
        for (String aShow : showsList) {
            int lowestSeason = showInfoController.findLowestSeason(aShow);
            showSettings.put(aShow, new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, lowestSeason))));
        }
        new FileManager().save(new UserSettings(userName, showSettings), Variables.UsersFolder, userName, Variables.UsersExtension, false);
    }

    // During the firstRun, This is ran which shows a popup to add directory to scan. You can exit this without entering anything. If you do enter one, it will then ask you if you want to add another, or move on.
    private void addDirectories() {
        boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        int index = 0;
        while (addAnother) {
            boolean[] matched = programSettingsController.addDirectory(index, textBox.addDirectoriesDisplay(Strings.PleaseEnterShowsDirectory, programSettingsController.getDirectories(), Strings.YouNeedToEnterADirectory, Strings.DirectoryIsInvalid, null));
            index++;
            if (!matched[0] && !matched[1]) {
                MessageBox messageBox = new MessageBox();
                messageBox.display(Strings.DirectoryWasADuplicate, Main.stage);
            } else if (matched[1]) {
                break;
            }
            if (!confirmBox.display(Strings.AddAnotherDirectory, Main.stage)) {
                addAnother = false;
            }
        }
    }

    public void generateShowsFile(int index, File folderLocation) {
        FileManager fileManager = new FileManager();
        if (!fileManager.checkFileExists(Variables.DirectoriesFolder, ("Directory-" + String.valueOf(index)), Variables.ShowsExtension)) {
            log.info("Generating ShowsFile for: " + folderLocation);
            FindShows findShows = new FindShows();
            Map<String, Show> shows = new HashMap<>();
            final ArrayList<String> ignoredShows;
            if (userInfoController.getAllUsers().isEmpty()) ignoredShows = new ArrayList<>();
            else ignoredShows = userInfoController.getIgnoredShows();
            findShows.findShows(folderLocation).forEach(aShow -> {
                log.info("Currently Processing: " + aShow);
                Map<Integer, Season> seasons = new HashMap<>();
                findShows.findSeasons(folderLocation, aShow).forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    Map<Integer, Episode> episodes = new HashMap<>();
                    ArrayList<String> episodesFull = findShows.findEpisodes(folderLocation, aShow, aSeason);
                    episodesFull.forEach(aEpisode -> {
                        log.info("Episode: " + aEpisode);
                        int[] episode = showInfoController.getEpisodeInfo(aEpisode);
                        if (episode != null && episode.length > 0) {
                            if (episode.length == 1) {
                                episodes.put(episode[0], new Episode(episode[0], (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), false));
                            } else if (episode.length == 2) {
                                episodes.put(episode[0], new Episode(episode[0], (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), true));
                                episodes.put(episode[1], new Episode(episode[1], (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode), true));
                            } else if (episode.length >= 3) {
                                log.warning("Error 1 if at this point!" + " + " + Arrays.toString(episode));
                            }
                        }
                    });
                    if (!episodes.isEmpty()) {
                        seasons.put(aSeason, new Season(aSeason, episodes));
                    }
                });
                if (!seasons.isEmpty()) {
                    shows.put(aShow, new Show(aShow, seasons));
                    if (ignoredShows.contains(aShow)) {
                        userInfoController.setIgnoredStatus(aShow, false);
                    }
                }
            });
            programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1);
            fileManager.save((Serializable) shows, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(index)), Variables.ShowsExtension, false);
        }
    }
}
