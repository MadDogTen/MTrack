package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryController {
    private final Logger log = Logger.getLogger(DirectoryController.class.getName());
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
    public boolean isDirectoryCurrentlyActive(File directory) { //TODO The must be a better way of doing this.
        String bsR1 = "\\\\";
        Pattern drivePattern = Pattern.compile("[A-Z]:" + bsR1);
        Pattern networkFolderPattern = Pattern.compile(bsR1 + bsR1 + "[0-9A-Za-z-]+" + bsR1);
        Matcher driveMatcher = drivePattern.matcher(directory.toString());
        Matcher networkFolderMatcher = networkFolderPattern.matcher(directory.toString());
        if (driveMatcher.find() || networkFolderMatcher.find()) {
            String match = Strings.EmptyString;
            if (driveMatcher.find()) {
                match = driveMatcher.group();
            } else if (networkFolderMatcher.find()) {
                match = networkFolderMatcher.group();
            }
            File baseDirectory = new File(match);
            return baseDirectory.exists() && baseDirectory.canRead() && directory.exists() && directory.canRead();
        }

        Pattern ipPattern = Pattern.compile(bsR1 + bsR1 + "[0-9a-z][0-9.a-z:]+[.|:][0-9.a-z:]+[0-9a-z]" + bsR1);
        Matcher ipMatcher = ipPattern.matcher(directory.toString());
        if (ipMatcher.find()) {
            return directory.exists() && directory.canRead() && directory.getTotalSpace() > 0;
        }
        log.severe("Error- Directory path format not currently supported, please report for the issue to be corrected. - " + directory);
        return false;
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
            saveDirectory(new Directory(directory, fileName, index, -1, new HashMap<>(), Main.getProgramSettingsController().getProgramGeneratedID()), false);
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
            //noinspection unchecked
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
        return new Directory(new File("Empty"), "Empty", -1, -1, new HashMap<>(), Main.getProgramSettingsController().getProgramGeneratedID());
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