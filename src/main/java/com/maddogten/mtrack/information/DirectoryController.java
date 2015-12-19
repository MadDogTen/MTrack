package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
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
    private boolean reloadShowsFile = false;

    // Saves all the directory paths that the program is currently set to check.
    public ArrayList<Directory> getDirectories() {
        ArrayList<Directory> directories = new ArrayList<>();
        File[] files = new File(Variables.dataFolder + Variables.DirectoriesFolder).listFiles();
        if (files != null) {
            for (File file : files) {
                String[] fileSplit = file.toString().split(Pattern.quote(Strings.FileSeparator));
                String properFileString = fileSplit[fileSplit.length - 1];
                directories.add((Directory) new FileManager().loadFile(Variables.DirectoriesFolder, properFileString, Strings.EmptyString));
            }
        }
        return directories;
    }

    // If it is able to find the directories, then it is found and returns true. If not found, returns false.
    public ArrayList<Directory> getActiveDirectories(boolean skipFoundInactiveDirectories) { //TODO There must be a better way of doing this.
        ArrayList<Directory> activeDirectories = new ArrayList<>();
        ArrayList<Directory> untestedDirectories = getDirectories();
        if (skipFoundInactiveDirectories) {
            log.info("skipFoundInactiveDirectories was true, Skipping known inactive directories.");
            if (inactiveDirectories.isEmpty()) log.info("inactiveDirectories was empty, checking all directories.");
            else {
                inactiveDirectories.forEach(directory -> {
                    Iterator<Directory> directoryIterator = untestedDirectories.listIterator();
                    while (directoryIterator.hasNext()) {
                        if (directory.equals(directoryIterator.next().getDirectory())) {
                            log.info(directory + " was skipped in active check as it was previously found as inactive.");
                            directoryIterator.remove();
                            break;
                        }
                    }
                });
            }
        } else log.info("skipFoundInactiveDirectories was false, Rechecking all directories.");
        String bsR1 = "\\\\";
        Pattern drivePattern = Pattern.compile("[A-Z]:" + bsR1);
        Pattern networkFolderPattern = Pattern.compile(bsR1 + bsR1 + "[0-9A-Za-z-]+" + bsR1);
        Pattern ipPattern = Pattern.compile(bsR1 + bsR1 + "[0-9a-z][0-9.a-z:]+[.|:][0-9.a-z:]+[0-9a-z]" + bsR1);
        untestedDirectories.forEach(directory -> {
            log.info("Checking active status of " + directory.getDirectory() + '.');
            Matcher driveMatcher = drivePattern.matcher(directory.getDirectory().toString());
            Matcher networkFolderMatcher = networkFolderPattern.matcher(directory.getDirectory().toString());
            String match = Strings.EmptyString;
            boolean[] isDirectoryActive = {false};
            boolean directoryChecked = false;
            if (driveMatcher.find()) {
                match = driveMatcher.group();
            } else if (networkFolderMatcher.find()) {
                match = networkFolderMatcher.group();
            }
            if (!match.isEmpty()) {
                log.info(directory.getDirectory().toString() + " was detected as being a drive/networked directory.");
                directoryChecked = true;
                File baseDirectory = new File(match);
                Task<Void> checkingTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        isDirectoryActive[0] = baseDirectory.exists() && baseDirectory.canRead() && directory.getDirectory().exists() && directory.getDirectory().canRead();
                        return null;
                    }
                };
                Thread thread = new Thread(checkingTask);
                thread.start();
                try { // This is to give the Thread a chance to finish before the while loop starts. Mainly to avoid the "Time remaining" message without using a boolean.
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e);
                }
                int timer = GenericMethods.getTimeSeconds();
                while (thread.isAlive()) {
                    log.info("Time remaining until directory is skipped = " + (Variables.timeToWaitForDirectory - GenericMethods.timeTakenSeconds(timer)));
                    if (Variables.timeToWaitForDirectory - GenericMethods.timeTakenSeconds(timer) < 1) {
                        log.info(directory.getDirectory() + " took to long to respond to alive check, Check that the drive is plugged in & working.");
                        thread.interrupt();
                        break;
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            GenericMethods.printStackTrace(log, e);
                        }
                    }
                    if (!Main.programRunning) {
                        thread.interrupt();
                    }
                }
            }
            if (!directoryChecked) {
                Matcher ipMatcher = ipPattern.matcher(directory.getDirectory().toString());
                if (ipMatcher.find()) {
                    log.info(directory.getDirectory() + " was detected as being an ip address.");
                    directoryChecked = true;
                    Task<Void> checkingTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            isDirectoryActive[0] = directory.getDirectory().exists() && directory.getDirectory().canRead() && directory.getDirectory().getTotalSpace() > 0;
                            return null;
                        }
                    };
                    Thread thread = new Thread(checkingTask);
                    thread.start();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        GenericMethods.printStackTrace(log, e);
                    }
                    int timer = GenericMethods.getTimeSeconds();
                    while (thread.isAlive()) {
                        log.info("Time remaining until directory is skipped = " + (Variables.timeToWaitForDirectory - GenericMethods.timeTakenSeconds(timer)));
                        if (Variables.timeToWaitForDirectory - GenericMethods.timeTakenSeconds(timer) < 1) {
                            log.info(directory.getDirectory() + " took to long to respond to alive check, Check that the drive is plugged in & working.");
                            thread.interrupt();
                            break;
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                GenericMethods.printStackTrace(log, e);
                            }
                        }
                        if (!Main.programRunning) {
                            thread.interrupt();
                        }
                    }
                }
            }
            if (directoryChecked) {
                if (!skipFoundInactiveDirectories && isDirectoryActive[0] && inactiveDirectories.contains(directory.getDirectory()))
                    inactiveDirectories.remove(directory.getDirectory());
                String activeStatus;
                if (isDirectoryActive[0]) {
                    activeStatus = "active.";
                    activeDirectories.add(directory);
                } else {
                    activeStatus = "inactive.";
                    Platform.runLater(() -> new MessageBox().display(new String[]{Strings.Warning + directory.getDirectory() + Strings.WasFoundToBeInactive, Strings.PleaseCorrectTheIssueThenForceRefresh}, null));
                    if (!inactiveDirectories.contains(directory.getDirectory())) {
                        log.info(directory.getDirectory() + " was added to the inactiveDirectories list.");
                        inactiveDirectories.add(directory.getDirectory());
                    }
                }
                log.info("Finished checking " + directory.getDirectory() + ". It was found to be " + activeStatus);
            } else
                log.severe("Error- Directory path format not currently supported, please report for the issue to be corrected. - " + directory.getDirectory());
        });
        return activeDirectories;
    }

    // Debugging tool - Prints all directories to console.

    public void printAllDirectories() {
        log.info("Printing out all directories:");
        if (getDirectories().isEmpty()) {
            log.info("No directories.");
        } else getDirectories().forEach(directory -> log.info(directory.getDirectory().toString()));
        log.info("Finished printing out all directories:");
    }

    // Add a new directory.
    public boolean[] addDirectory(int index, File directory) {
        ArrayList<Directory> directories = getDirectories();
        boolean[] answer = {false, false};
        if (directories == null) {
            directories = new ArrayList<>();
        }
        boolean directoryAlreadyExists = false;
        for (Directory aDirectory : directories) {
            if (aDirectory.getDirectory() == directory) {
                directoryAlreadyExists = true;
                break;
            }
        }
        if (!directory.toString().isEmpty() && !directoryAlreadyExists) {
            log.info("Added Directory");
            String[] splitResult = String.valueOf(directory).split(Pattern.quote(Strings.FileSeparator));
            String fileName = "";
            for (String singleSplit : splitResult) {
                if (singleSplit.contains(":")) {
                    singleSplit = singleSplit.replace(":", "");
                }
                if (!singleSplit.isEmpty()) {
                    if (fileName.isEmpty()) {
                        fileName = singleSplit;
                    } else fileName += '_' + singleSplit;
                }
            }
            saveDirectory(new Directory(directory, fileName, index, -1, new HashMap<>(), Main.getProgramSettingsController().getSettingsFile().getProgramSettingsID()), false);
            answer[0] = true;
        } else if (directory.toString().isEmpty()) {
            answer[1] = true;
        }
        return answer;
    }

    // Returns the lowest usable directory index. If directory is deleted this make the index reusable.
    public int getLowestFreeDirectoryIndex() {
        final int[] lowestFreeIndex = {0};
        final boolean[] first = {true};
        getDirectories().forEach(directory -> {
            int index = directory.getIndex();
            if (first[0]) {
                lowestFreeIndex[0] = index;
                first[0] = false;
            } else if (index < lowestFreeIndex[0]) {
                lowestFreeIndex[0] = index;
            }
        });
        return lowestFreeIndex[0];
    }

    // Loads all the directory files. You can tell it to skip a particular directory if you don't need it.
    public ArrayList<Directory> getDirectories(int skip) {
        // ArrayList = Shows list from all added Directories
        if (skip == -2)
            return getDirectories();
        else {
            ArrayList<Directory> directories = getDirectories();
            Iterator<Directory> directoryIterator = directories.iterator();
            while (directoryIterator.hasNext()) {
                if (directoryIterator.next().getIndex() == skip) {
                    directoryIterator.remove();
                    break;
                }
            }
            return directories;
        }
    }

    // Gets a single directory map using the given index.
    @SuppressWarnings("unchecked")
    public Directory getDirectory(int index) {
        for (Directory directory : getDirectories()) {
            if (directory.getIndex() == index) {
                return directory;
            }
        }
        log.warning("Warning- If this point is reached, please report.");
        return new Directory(new File("Empty"), "Empty", -1, -1, new HashMap<>(), Main.getProgramSettingsController().getSettingsFile().getProgramSettingsID());
    }

    public void saveDirectory(Directory directory, Boolean loadMap) {
        new FileManager().save(directory, Variables.DirectoriesFolder, directory.getFileName(), Variables.ShowsExtension, true);
        if (loadMap) {
            reloadShowsFile = true;
        }
    }

    // Removes a directory. While doing that, it checks if the shows are still found else where, and if not, sets the show to ignored, then updates the Controller tableViewField to recheck the remaining field.
    public void removeDirectory(Directory aDirectory) {
        log.info("Currently processing removal of: " + aDirectory.getFileName());
        new FileManager().deleteFile(Variables.dataFolder + Variables.DirectoriesFolder, aDirectory.getFileName(), Variables.ShowsExtension);
        log.info("Finished processing removal of the directory.");
    }

    public boolean isReloadShowsFile() {
        return reloadShowsFile;
    }

    @SuppressWarnings("SameParameterValue")
    public void setReloadShowsFile(boolean reloadShowsFile) {
        this.reloadShowsFile = reloadShowsFile;
    }
}