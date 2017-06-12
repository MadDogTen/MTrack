package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Database.DBDirectoryHandler;
import com.maddogten.mtrack.Database.DBManager;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryController {
    private final Logger log = Logger.getLogger(DirectoryController.class.getName());
    private DBDirectoryHandler dbDirectoryHandler;
    private int lastActiveCheck = -2;
    private boolean lastSearchSkip = true;

    public void initDatabase(DBManager dbManager) {
        dbDirectoryHandler = new DBDirectoryHandler(dbManager);
    }


    // If it is able to find the directories, then it is found and returns true. If not found, returns false.
    public void checkDirectories(boolean skipKnownInactiveDirectories) {
        Set<Integer> directories;
        if (lastActiveCheck == -2 || GenericMethods.timeTakenSeconds(lastActiveCheck) > Variables.timeBetweenRecheckingActiveDirectoriesSeconds || (lastSearchSkip && !skipKnownInactiveDirectories)) {
            if (skipKnownInactiveDirectories) {
                log.finer("skipFoundInactiveDirectories was true, Skipping known inactive directories.");
                directories = dbDirectoryHandler.getActiveDirectories();
            } else {
                log.finer("skipFoundInactiveDirectories was false, Rechecking all directories.");
                directories = dbDirectoryHandler.getAllDirectories();
            }
            String bsR1 = "\\\\";
            Pattern drivePattern = Pattern.compile("[A-Z]:" + bsR1);
            Pattern networkFolderPattern = Pattern.compile(bsR1 + bsR1 + "[0-9A-Za-z-]+" + bsR1);
            Pattern ipPattern = Pattern.compile(bsR1 + bsR1 + "[0-9a-z][0-9.a-z:]+[.|:][0-9.a-z:]+[0-9a-z]" + bsR1);
            directories.forEach(directoryID -> {
                File directory = new File(dbDirectoryHandler.getDirectory(directoryID));
                log.finer("Checking active status of " + dbDirectoryHandler.getDirectory(directoryID) + '.');
                Matcher driveMatcher = drivePattern.matcher(directory.toString());
                Matcher networkFolderMatcher = networkFolderPattern.matcher(directory.toString());
                Matcher ipMatcher = ipPattern.matcher(directory.toString());
                boolean[] isDirectoryActive = {false};
                boolean directoryMatchFound = false;
                File baseDirectory;
                baseDirectory = driveMatcher.find() ? new File(driveMatcher.group()) : networkFolderMatcher.find() ? new File(networkFolderMatcher.group()) : ipMatcher.find() ? directory : null;
                if (baseDirectory != null) {
                    boolean ipDrive = baseDirectory == directory;
                    if (ipDrive) log.finer(directory + " was detected as being an ip address.");
                    else
                        log.finer(directory + " was detected as being a drive/networked directory.");
                    directoryMatchFound = true;
                    Task<Void> checkingTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            if (ipDrive)
                                isDirectoryActive[0] = directory.exists() && directory.canRead() && directory.getTotalSpace() > 0;
                            else
                                isDirectoryActive[0] = baseDirectory.exists() && baseDirectory.canRead() && directory.exists() && directory.canRead();
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
                        log.finer("Time remaining until directory is skipped = " + (ClassHandler.userInfoController().getTimeToWaitForDirectory(Variables.getCurrentUser()) - GenericMethods.timeTakenSeconds(timer)));
                        if (ClassHandler.userInfoController().getTimeToWaitForDirectory(Variables.getCurrentUser()) - GenericMethods.timeTakenSeconds(timer) < 1) {
                            log.finer(directory + " took to long to respond to alive check, Check that the drive is plugged in & working.");
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
                    String activeStatus;
                    if (isDirectoryActive[0]) {
                        activeStatus = "active.";
                        if (!dbDirectoryHandler.isDirectoryActive(directoryID))
                            dbDirectoryHandler.setDirectoryActiveStatus(directoryID, true);
                    } else {
                        activeStatus = "inactive.";
                        Platform.runLater(() -> new MessageBox(new StringProperty[]{new SimpleStringProperty(Strings.Warning.getValue() + directory + Strings.WasFoundToBeInactive.getValue()), Strings.PleaseCorrectTheIssueThenForceRefresh}, null));
                        if (dbDirectoryHandler.isDirectoryActive(directoryID)) {
                            log.finer(directory + " was added to the inactiveDirectories list.");
                            dbDirectoryHandler.setDirectoryActiveStatus(directoryID, false);
                        }
                    }
                    log.info("Finished checking \"" + directory + "\". It was found to be " + activeStatus);
                } else
                    log.severe("Error- Directory path format not currently supported, please report for the issue to be corrected. - " + directory);
            });
            lastActiveCheck = GenericMethods.getTimeSeconds();
            lastSearchSkip = skipKnownInactiveDirectories;
        } else
            log.info("Already checked for active directories within " + Variables.timeBetweenRecheckingActiveDirectoriesSeconds + " seconds, Skipping check.");
    }

    // Add a new directory.
    public int addDirectory(final File folder) {
        int answer = -2;
        if (dbDirectoryHandler.addDirectory(folder, true))
            answer = dbDirectoryHandler.getDirectoryID(folder.toString());
        return answer;
    }

    // TODO Finish directory removal
    // Removes a directory. While doing that, it checks if the shows are still found else where, and if not, sets the show to ignored, then updates the Controller tableViewField to recheck the remaining field.
    /*public void removeDirectory(final Directory aDirectory) {
        log.info("Currently processing removal of: " + aDirectory.getFileName());
        if (!new FileManager().deleteFile(Variables.DirectoriesFolder, aDirectory.getFileName(), Variables.ShowFileExtension))
            log.info("Wasn't able to delete directory.");
        log.info("Finished processing removal of the directory.");
    }*/

    public File getDirectoryFromID(int directoryID) {
        return new File(dbDirectoryHandler.getDirectory(directoryID));
    }

    public int getDirectoryWithLowestPriority(Set<Integer> directories) {
        return dbDirectoryHandler.getDirectoryWithLowestPriorityFromList(directories);
    }

    public Set<Integer> getAllDirectories(boolean recheck, boolean skipKnownInactiveDirectories) {
        if (recheck) checkDirectories(skipKnownInactiveDirectories);
        return dbDirectoryHandler.getAllDirectories();
    }

    public Set<Integer> getActiveDirectories(boolean recheck, boolean skipKnownInactiveDirectories) {
        if (recheck) checkDirectories(skipKnownInactiveDirectories);
        return dbDirectoryHandler.getActiveDirectories();
    }

    public Set<Integer> getInactiveDirectories(boolean recheck, boolean skipKnownInactiveDirectories) {
        if (recheck) checkDirectories(skipKnownInactiveDirectories);
        return dbDirectoryHandler.getInactiveDirectories();
    }
}
