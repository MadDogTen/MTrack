package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryController {
    private final Logger log = Logger.getLogger(DirectoryController.class.getName());
    private final ArrayList<File> inactiveDirectories = new ArrayList<>();
    private int lastActiveCheck = -2;

    // Saves all the directory paths that the program is currently set to check.
    public ArrayList<Directory> findDirectories(final boolean includeInactive, final boolean skipFoundInactiveDirectories, final boolean skipRechecking) {
        ArrayList<Directory> directories = new ArrayList<>();
        File[] files = new File(Variables.dataFolder + Variables.DirectoriesFolder).listFiles();
        if (files != null) {
            for (File file : files) {
                String[] fileSplit = file.toString().split(Pattern.quote(Strings.FileSeparator));
                String properFileString = fileSplit[fileSplit.length - 1];
                Directory directory = (Directory) new FileManager().loadFile(Variables.DirectoriesFolder, properFileString, Strings.EmptyString);
                if (directory != null)
                    directories.add((Directory) new FileManager().loadFile(Variables.DirectoriesFolder, properFileString, Strings.EmptyString));
            }
        }

        if (skipRechecking && !includeInactive) {
            ArrayList<Directory> activeOnly = new ArrayList<>();
            directories.stream().filter(directory -> !inactiveDirectories.contains(directory.getDirectory())).forEach(activeOnly::add);
            return activeOnly;
        }

        return includeInactive ? directories : getActiveDirectories(directories, skipFoundInactiveDirectories);
    }

    // Loads all the directory files. You can tell it to skip a particular directory if you don't need it.
    public ArrayList<Directory> findDirectories(final long skip, final boolean includeInactive, final Boolean skipFoundInactiveDirectories, @SuppressWarnings("SameParameterValue") final boolean skipRechecking) {
        // ArrayList = Shows list from all added Directories
        if (skip == -2) return findDirectories(includeInactive, skipFoundInactiveDirectories, skipRechecking);
        else {
            ArrayList<Directory> directories = findDirectories(includeInactive, skipFoundInactiveDirectories, skipRechecking);
            Iterator<Directory> directoryIterator = directories.iterator();
            while (directoryIterator.hasNext()) {
                if (directoryIterator.next().getDirectoryID() == skip) {
                    directoryIterator.remove();
                    break;
                }
            }
            return directories;
        }
    }

    // If it is able to find the directories, then it is found and returns true. If not found, returns false.
    private ArrayList<Directory> getActiveDirectories(final ArrayList<Directory> untestedDirectories, final boolean skipFoundInactiveDirectories) {
        ArrayList<Directory> activeDirectories = new ArrayList<>(untestedDirectories.size());
        if (lastActiveCheck == -2 || GenericMethods.timeTakenSeconds(lastActiveCheck) > Variables.timeBetweenRecheckingActiveDirectoriesSeconds) {
            if (skipFoundInactiveDirectories) {
                log.finer("skipFoundInactiveDirectories was true, Skipping known inactive directories.");
                if (inactiveDirectories.isEmpty())
                    log.finer("inactiveDirectories was empty, checking all directories.");
                else {
                    inactiveDirectories.forEach(directory -> {
                        Iterator<Directory> directoryIterator = untestedDirectories.listIterator();
                        while (directoryIterator.hasNext()) {
                            if (directory.equals(directoryIterator.next().getDirectory())) {
                                log.finer(directory + " was skipped in active check as it was previously found as inactive.");
                                directoryIterator.remove();
                                break;
                            }
                        }
                    });
                }
            } else log.finer("skipFoundInactiveDirectories was false, Rechecking all directories.");
            final boolean[] reloadShowFile = {false};
            String bsR1 = "\\\\";
            Pattern drivePattern = Pattern.compile("[A-Z]:" + bsR1);
            Pattern networkFolderPattern = Pattern.compile(bsR1 + bsR1 + "[0-9A-Za-z-]+" + bsR1);
            Pattern ipPattern = Pattern.compile(bsR1 + bsR1 + "[0-9a-z][0-9.a-z:]+[.|:][0-9.a-z:]+[0-9a-z]" + bsR1);
            ArrayList<File> previouslyInactive = new ArrayList<>(inactiveDirectories);
            untestedDirectories.forEach(directory -> {
                log.finer("Checking active status of " + directory.getDirectory() + '.');
                Matcher driveMatcher = drivePattern.matcher(directory.getDirectory().toString());
                Matcher networkFolderMatcher = networkFolderPattern.matcher(directory.getDirectory().toString());
                Matcher ipMatcher = ipPattern.matcher(directory.getDirectory().toString());
                boolean[] isDirectoryActive = {false};
                boolean directoryMatchFound = false;
                File baseDirectory;
                baseDirectory = driveMatcher.find() ? new File(driveMatcher.group()) : networkFolderMatcher.find() ? new File(networkFolderMatcher.group()) : ipMatcher.find() ? directory.getDirectory() : null;
                if (baseDirectory != null) {
                    boolean ipDrive = baseDirectory == directory.getDirectory();
                    if (ipDrive) log.finer(directory.getDirectory() + " was detected as being an ip address.");
                    else
                        log.finer(directory.getDirectory() + " was detected as being a drive/networked directory.");
                    directoryMatchFound = true;
                    Task<Void> checkingTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            if (ipDrive)
                                isDirectoryActive[0] = directory.getDirectory().exists() && directory.getDirectory().canRead() && directory.getDirectory().getTotalSpace() > 0;
                            else
                                isDirectoryActive[0] = baseDirectory.exists() && baseDirectory.canRead() && directory.getDirectory().exists() && directory.getDirectory().canRead();
                            return null;
                        }
                    };
                    Thread thread = new Thread(checkingTask);
                    thread.start();
                    int timer = GenericMethods.getTimeSeconds();
                    try { // This is to give the Thread a chance to finish before the while loop starts. Mainly to avoid the "Time remaining" message.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        GenericMethods.printStackTrace(log, e, this.getClass());
                    }
                    while (thread.isAlive()) {
                        log.finer("Time remaining until directory is skipped = " + (Variables.timeToWaitForDirectory - GenericMethods.timeTakenSeconds(timer)));
                        if (Variables.timeToWaitForDirectory - GenericMethods.timeTakenSeconds(timer) < 1) {
                            log.finer(directory.getDirectory() + " took to long to respond to alive check, Check that the drive is plugged in & working.");
                            thread.interrupt();
                            break;
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                GenericMethods.printStackTrace(log, e, this.getClass());
                            }
                        }
                        if (!Main.programRunning) thread.interrupt();
                    }
                }
                if (directoryMatchFound) {
                    if (!skipFoundInactiveDirectories && isDirectoryActive[0] && previouslyInactive.contains(directory.getDirectory())) {
                        inactiveDirectories.remove(directory.getDirectory());
                        if (!reloadShowFile[0]) reloadShowFile[0] = true;
                    }
                    String activeStatus;
                    if (isDirectoryActive[0]) {
                        activeStatus = "active.";
                        activeDirectories.add(directory);
                    } else {
                        activeStatus = "inactive.";
                        Platform.runLater(() -> new MessageBox(new StringProperty[]{new SimpleStringProperty(Strings.Warning.getValue() + directory.getDirectory() + Strings.WasFoundToBeInactive.getValue()), Strings.PleaseCorrectTheIssueThenForceRefresh}, null));
                        if (!inactiveDirectories.contains(directory.getDirectory())) {
                            log.finer(directory.getDirectory() + " was added to the inactiveDirectories list.");
                            inactiveDirectories.add(directory.getDirectory());
                            if (!reloadShowFile[0]) reloadShowFile[0] = true;
                        }
                    }
                    log.info("Finished checking \"" + directory.getDirectory() + "\". It was found to be " + activeStatus);
                } else
                    log.severe("Error- Directory path format not currently supported, please report for the issue to be corrected. - " + directory.getDirectory());
            });
            if (reloadShowFile[0]) {
                log.finer("Inactive directories list was changed, updating ShowsFile & Displayed list (If applicable)");
                ClassHandler.showInfoController().loadShowsFile(activeDirectories);
                if (Main.programFullyRunning) {
                    inactiveDirectories.stream().filter(file -> !previouslyInactive.contains(file)).forEach(file -> untestedDirectories.stream().filter(directory -> directory.getDirectory() == file).forEach(directory -> directory.getShows().forEach((showName, aShow) -> Controller.updateShowField(showName, ClassHandler.showInfoController().doesShowExistElsewhere(showName, activeDirectories)))));
                    previouslyInactive.stream().filter(file -> !inactiveDirectories.contains(file)).forEach(file -> untestedDirectories.stream().filter(directory -> directory.getDirectory() == file).forEach(directory -> directory.getShows().forEach((showName, aShow) -> Controller.updateShowField(showName, true))));
                }
            }
            lastActiveCheck = GenericMethods.getTimeSeconds();
        } else {
            log.info("Already checked for active directories within " + Variables.timeBetweenRecheckingActiveDirectoriesSeconds + " seconds, Skipping check.");
            untestedDirectories.stream().filter(directory -> !inactiveDirectories.contains(directory.getDirectory())).forEach(activeDirectories::add);
        }

        return activeDirectories;
    }

    // Add a new directory.
    public Long[] addDirectory(final File folder) {
        ArrayList<Directory> directories = findDirectories(true, false, true);
        Long[] answer = {null, null};
        boolean directoryDoesNotExist = true;
        for (Directory aDirectory : directories) {
            if (aDirectory.getDirectory() == folder) {
                directoryDoesNotExist = false;
                break;
            }
        }
        if (!folder.toString().isEmpty() && directoryDoesNotExist) {
            log.info("Added Directory");
            String[] splitResult = String.valueOf(folder).split(Pattern.quote(Strings.FileSeparator));
            StringBuilder fileName = new StringBuilder();
            for (String singleSplit : splitResult) {
                if (singleSplit.contains(":")) singleSplit = singleSplit.replace(":", "");
                if (!singleSplit.isEmpty()) {
                    if (fileName.length() == 0) fileName = new StringBuilder(singleSplit);
                    else fileName.append('_').append(singleSplit);
                }
            }
            Directory directory = new Directory(folder, fileName.toString(), -2, new HashMap<>());
            saveDirectory(directory, false);
            answer[0] = directory.getDirectoryID();
        } else if (folder.toString().isEmpty()) answer[1] = (long) -1;
        return answer;
    }

    // Gets a single directory map using the given index.
    public Directory getDirectory(final long directoryID) {
        for (Directory directory : findDirectories(true, false, true)) {
            if (directory.getDirectoryID() == directoryID) return directory;
        }
        log.warning("Warning- If this point is reached, please report.");
        return new Directory(new File("Empty"), "Empty", -1, new HashMap<>());
    }

    public void saveDirectory(final Directory directory, final Boolean loadMap) {
        new FileManager().save(directory, Variables.DirectoriesFolder, directory.getFileName(), Variables.ShowFileExtension, true);
        if (loadMap)
            ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, true));
    }

    // Removes a directory. While doing that, it checks if the shows are still found else where, and if not, sets the show to ignored, then updates the Controller tableViewField to recheck the remaining field.
    public void removeDirectory(final Directory aDirectory) {
        log.info("Currently processing removal of: " + aDirectory.getFileName());
        if (!new FileManager().deleteFile(Variables.DirectoriesFolder, aDirectory.getFileName(), Variables.ShowFileExtension))
            log.info("Wasn't able to delete directory.");
        log.info("Finished processing removal of the directory.");
    }
}
