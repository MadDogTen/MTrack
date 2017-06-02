package com.maddogten.mtrack.io;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.gui.MultiChoice;
import com.maddogten.mtrack.gui.TextBox;
import com.maddogten.mtrack.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
      FileManager handles the saving and loading of files, checking if files exist, as well as accessing other things needed from the OS.
 */

public class FileManager {
    private final Logger log = Logger.getLogger(FileManager.class.getName());

    public void save(final Serializable objectToSerialise, final String folder, final String filename, final String extension, final boolean overWrite) {
        if (!new File(Variables.dataFolder + folder).isDirectory()) createFolder(folder);
        if (overWrite || !checkFileExists(folder, filename, extension)) {
            String file = Variables.dataFolder + folder + Strings.FileSeparator + filename + extension;
            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file + Variables.TempExtension))) {
                    oos.writeObject(objectToSerialise);
                    oos.close();
                    File fileNew = new File(file + Variables.TempExtension);
                    if (fileNew.exists()) {
                        File fileOld = new File(file);
                        if (fileOld.exists()) deleteFile(fileOld, true);
                        if (!fileNew.renameTo(new File(file))) {
                            Files.move(Paths.get(file + Variables.TempExtension), Paths.get(file));
                        }
                    }
                }
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        } else log.info(filename + " save already exists.");
    }

    public Object loadFile(final String folder, final String theFile, final String extension) {
        if (checkFileExists(folder, theFile, extension)) {
            Object loadedFile = null;
            try {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension)))) {
                    loadedFile = ois.readObject();
                } catch (EOFException e) {
                    log.severe("\"" + Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension + "\" was corrupt, Please correct the issue and try again.");
                    new MessageBox(new StringProperty[]{(new SimpleStringProperty("\"" + Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension + "\" was corrupt, Please correct the issue and try again."))}, null);
                }
            } catch (ClassNotFoundException | IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
            return loadedFile;
        } else if (!extension.endsWith(Variables.TempExtension) & checkFileExists(folder, theFile, extension + Variables.TempExtension)) {
            try {
                Files.move(Paths.get(Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension + Variables.TempExtension), Paths.get(Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension));
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, getClass());
            }
            if (checkFileExists(folder, theFile, extension)) return loadFile(folder, theFile, extension);
        }
        log.info("File doesn't exist - " + (Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension));
        //noinspection ReturnOfNull
        return null;
    }

    public boolean checkFileExists(final String folder, final String filename, final String extension) {
        return new File(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension).isFile();
    }

    public boolean checkFolderExistsAndReadable(final File aFolder) {
        return aFolder.isDirectory() && aFolder.canRead();
    }

    public void createFolder(final String folder) {
        if (!new File(Variables.dataFolder + folder).mkdir())
            log.warning("Cannot make: " + Variables.dataFolder + folder);
        log.info("Created folder: " + folder);
    }

    public File getJarLocationFolder() throws UnsupportedEncodingException {
        File file = new File(URLDecoder.decode(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
        StringBuilder converted = new StringBuilder(Strings.EmptyString);
        String[] split = String.valueOf(file).split(Pattern.quote(Strings.FileSeparator));
        for (String splitPart : split) {
            if (converted.length() == 0) converted = new StringBuilder(splitPart);
            else if (!splitPart.matches(split[split.length - 1]))
                converted.append(Strings.FileSeparator).append(splitPart);
        }
        return new File(converted.toString());
    }

    // This is to make sure it doesn't delete files other than ones generated by this program if they are stored in jar location (For example, On the desktop/in the downloads folder).
    public void clearProgramFiles() {
        if (Variables.dataFolder.toString().matches(Pattern.quote(String.valueOf(OperatingSystem.programFolder)))) {
            log.info("Deleting " + Variables.dataFolder + " in AppData...");
            GenericMethods.stopFileLogging(log);
            deleteFolder(Variables.dataFolder);
        } else {
            log.info("Deleting appropriate files found in the folder the jar is contained in...");
            GenericMethods.stopFileLogging(log);
            File[] files = Variables.dataFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean valid = false;
                    if (file.toString().endsWith(Variables.SettingFileExtension)) valid = true;
                    else {
                        String[] splitFile = file.toString().split(Pattern.quote(Strings.FileSeparator));
                        String directory = Strings.FileSeparator + splitFile[splitFile.length - 1];
                        if (directory.matches(Pattern.quote(Variables.DirectoriesFolder)) || directory.matches(Pattern.quote(Variables.UsersFolder)) || directory.matches(Pattern.quote(Variables.LogsFolder)))
                            valid = true;
                    }
                    if (valid) {
                        if (file.isDirectory()) deleteFolder(file);
                        else deleteFile(file, false);
                    }
                }
            }
        }
    }

    private boolean deleteFile(final File file, final boolean suppressMessage) {
        if (file.exists() && file.isFile()) {
            if (file.canWrite() && file.delete() && !file.exists()) {
                if (!suppressMessage) log.info("\"" + file + "\" was successfully deleted.");
                return true;
            } else log.warning("Cannot delete: " + file);
        } else log.info("File " + file + " does not exist!");
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean deleteFile(final String folder, final String filename, final String extension) {
        if (checkFileExists(folder, filename, extension)) {
            File toDelete = new File(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension);
            if (toDelete.isFile() && toDelete.canWrite() && toDelete.delete() && !toDelete.exists()) {
                log.info("\"" + toDelete + "\" was successfully deleted.");
                return true;
            } else log.warning("Cannot delete: " + toDelete);
        } else
            log.warning("File " + Variables.dataFolder + folder + Strings.FileSeparator + filename + extension + " does not exist!");
        return false;
    }

    public void deleteFolder(final File toDeleteFolder) {
        if (!checkFolderExistsAndReadable(toDeleteFolder)) log.warning(toDeleteFolder + " does not exist!");
        if (toDeleteFolder.canWrite()) {
            if (toDeleteFolder.list().length == 0) {
                if (!toDeleteFolder.delete()) log.warning("Cannot delete: " + toDeleteFolder);
            } else {
                File[] files = toDeleteFolder.listFiles();
                if (files != null) {
                    for (File aFile : files) {
                        if (aFile.isFile() && deleteFile(aFile, false)) log.info(aFile + " was deleted.");
                        else if (aFile.isDirectory()) deleteFolder(aFile);
                    }
                }
                if (!toDeleteFolder.delete()) log.warning("Cannot delete: " + toDeleteFolder);
            }
        } else log.warning(toDeleteFolder + " is write protected!");
    }

    public boolean openFolder(final File file) {
        try {
            Desktop.getDesktop().open(file);
            return true;
        } catch (IOException e) {
            GenericMethods.printStackTrace(log, e, getClass());
        }
        return false;
    }


    public void exportSettings(final Stage stage) {
        log.info("exportSettings has been started.");
        ArrayList<String> choices = new MultiChoice().multipleCheckbox(new StringProperty[]{Strings.ChooseWhatToExport}, new StringProperty[]{Strings.All, Strings.Program, Strings.Users, Strings.Directories}, null, Strings.All, false, stage);
        if (choices.isEmpty()) log.info("No choices were selected, Noting exported");
        else {
            ArrayList<String> fileList = new ArrayList<>();
            if (choices.contains(Strings.All.getValue()) || choices.contains(Strings.Program.getValue()))
                fileList.add(Variables.dataFolder + Strings.FileSeparator + Strings.SettingsFileName + Variables.SettingFileExtension);
            if (choices.contains(Strings.All.getValue()) || choices.contains(Strings.Directories.getValue())) {
                Arrays.asList(new File(Variables.dataFolder + Strings.FileSeparator + Variables.DirectoriesFolder).list()).forEach(aFile -> {
                            if (aFile.endsWith(Variables.ShowFileExtension))
                                fileList.add(Variables.dataFolder + Strings.FileSeparator + Variables.DirectoriesFolder + Strings.FileSeparator + aFile);
                        }
                );
            }
            if (choices.contains(Strings.All.getValue()) || choices.contains(Strings.Users.getValue())) {
                Arrays.asList(new File(Variables.dataFolder + Strings.FileSeparator + Variables.UsersFolder).list()).forEach(aFile -> {
                    if (aFile.endsWith(Variables.UserFileExtension))
                        fileList.add(Variables.dataFolder + Strings.FileSeparator + Variables.UsersFolder + Strings.FileSeparator + aFile);
                });
            }

            if (fileList.isEmpty()) log.info("Nothing found to export - Must be an error.");
            else {
                try {
                    File file = new TextBox().pickFile(Strings.EnterLocationToSaveExport, new SimpleStringProperty("MTrackExport"), new StringProperty[]{new SimpleStringProperty("MTrack (*.MTrack)")}, new String[]{".MTrack"}, true, stage);
                    if (!file.toString().isEmpty()) {
                        log.info("Directory to save export in: \"" + file + "\'.");
                        byte[] buffer = new byte[1024];
                        try (FileOutputStream fileOutputStream = new FileOutputStream(file); ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
                            fileList.forEach(file1 -> {
                                try {
                                    File srcFile = new File(file1);
                                    try (FileInputStream fileInputStream = new FileInputStream(srcFile)) {
                                        String nameTrimmed = srcFile.getPath().substring(Variables.dataFolder.getPath().length() + 1, srcFile.getPath().length());
                                        log.info(nameTrimmed);
                                        zipOutputStream.putNextEntry(new ZipEntry(nameTrimmed));
                                        int length;
                                        while ((length = fileInputStream.read(buffer)) > 0) {
                                            zipOutputStream.write(buffer, 0, length);
                                        }
                                        zipOutputStream.closeEntry();
                                    }
                                } catch (Exception e) {
                                    GenericMethods.printStackTrace(log, e, FileManager.class);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    GenericMethods.printStackTrace(log, e, FileManager.class);
                }
            }
        }
        log.info("exportSettings has finished.");
    }

    public boolean importSettings(final boolean firstRun, final Stage stage) {
        log.info("importSettings has been started.");
        boolean result = false, showRestartWindow = true;
        File importFile = new TextBox().pickFile(Strings.EnterFileLocation, new SimpleStringProperty(Strings.EmptyString), new StringProperty[]{new SimpleStringProperty("MTrack (*.MTrack)")}, new String[]{".MTrack"}, false, stage);
        if (importFile.toString().isEmpty()) log.info("importFile was empty, Nothing imported.");
        else {
            // Start of custom code for personal use only.
            if (!firstRun && importFile.getName().endsWith(".xml")) {
                /*log.info("XML importing has started.");
                try {
                    NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(importFile).getElementsByTagName("Series");
                    Set<Integer> shows = new HashSet<>();
                    for (int x = 0; x < nodeList.getLength(); x++) {
                        Node node = nodeList.item(x);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String show = element.getElementsByTagName("SeriesName").item(0).getTextContent();
                            int[] showInfo = ClassHandler.showInfoController().addShow(show);
                            int showID = showInfo[0];
                            shows.add(showID);
                            log.info("Found Show in XML: \"" + show + "\".");
                            int season = Integer.parseInt(element.getElementsByTagName("Season").item(0).getTextContent()), episode = Integer.parseInt(element.getElementsByTagName("Episode").item(0).getTextContent()) + 1;
                            if (userSettings.getShowSettings().containsKey(show)) {
                                log.info("Show: \"" + show + "\" was found in user file, Updating Season & Episode ...");
                                if (!userSettings.getShowSettings().get(show).isActive())
                                    userSettings.getShowSettings().get(show).setActive(true);
                                userSettings.getShowSettings().get(show).setCurrentSeason(season);
                                userSettings.getShowSettings().get(show).setCurrentEpisode(episode);
                            } else {
                                log.info("Show: \"" + show + "\" wasn't found in user file, Adding...");
                                userSettings.getShowSettings().put(show, new UserShowSettings(show, true, false, false, season, episode));
                            }
                            log.info(show + " |||| " + season + " || " + episode);
                        }
                    }
                    if (!shows.isEmpty()) shows.forEach(show -> Controller.updateShowField(show, true));
                    showRestartWindow = false;
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    GenericMethods.printStackTrace(log, e, getClass());
                }
                log.info("XML importing is now finished.");*/
            } // end of custom code for personal use only.
            else {
                Set<String> importedStrings = new LinkedHashSet<>();
                try (FileReader fileReader = new FileReader(importFile);
                     BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) importedStrings.add(line);
                    result = true;
                } catch (Exception e) {
                    GenericMethods.printStackTrace(log, e, FileManager.class);
                }

                boolean loadedProgramSettings = false;
                String language = Strings.EmptyString;
                int updateSpeed = 0;
                boolean enableAutomaticShowUpdating = false;
                int timeToWaitForDirectory = 0;
                boolean show0Remaining = false;
                boolean showActiveShows = false;
                boolean isRecordChangesForNonActiveShows = false;
                boolean isRecordChangesForSeasonsLowerThanCurrent = false;
                boolean isStageMoveWithParentAndBlockParent = true;
                boolean isEnableSpecialEffects = true;
                boolean isFileLogging = true;
                float showColumnWidth = Variables.SHOWS_COLUMN_WIDTH;
                float remainingColumnWidth = Variables.REMAINING_COLUMN_WIDTH;
                float seasonColumnWidth = Variables.SEASONS_COLUMN_WIDTH;
                float episodeColumnWidth = Variables.EPISODE_COLUMN_WIDTH;
                boolean showColumnVisibility = true;
                boolean remainingColumnVisibility = true;
                boolean seasonColumnVisibility = false;
                boolean episodeColumnVisibility = false;
                for (String string : importedStrings) {
                    String[] settings = string.split("<>");
                    int i = 0;
                    switch (settings[0]) {
                        case "PROGRAM_SETTINGS_START":
                            language = settings[++i];
                            updateSpeed = Integer.parseInt(settings[++i]);
                            enableAutomaticShowUpdating = !Boolean.getBoolean(settings[++i]);
                            timeToWaitForDirectory = Integer.parseInt(settings[++i]);
                            show0Remaining = Boolean.getBoolean(settings[++i]);
                            showActiveShows = Boolean.getBoolean(settings[++i]);
                            isRecordChangesForNonActiveShows = Boolean.getBoolean(settings[++i]);
                            isRecordChangesForSeasonsLowerThanCurrent = Boolean.getBoolean(settings[++i]);
                            isStageMoveWithParentAndBlockParent = Boolean.getBoolean(settings[++i]);
                            isEnableSpecialEffects = Boolean.getBoolean(settings[++i]);
                            isFileLogging = Boolean.getBoolean(settings[++i]);
                            showColumnWidth = Float.parseFloat(settings[++i]);
                            remainingColumnWidth = Float.parseFloat(settings[++i]);
                            seasonColumnWidth = Float.parseFloat(settings[++i]);
                            episodeColumnWidth = Float.parseFloat(settings[++i]);
                            showColumnVisibility = Boolean.getBoolean(settings[++i]);
                            remainingColumnVisibility = Boolean.getBoolean(settings[++i]);
                            seasonColumnVisibility = Boolean.getBoolean(settings[++i]);
                            episodeColumnVisibility = Boolean.getBoolean(settings[++i]);
                            log.info(language + " | " + updateSpeed + " | " + enableAutomaticShowUpdating + " | "
                                    + timeToWaitForDirectory + " | " + show0Remaining + " | " + showActiveShows + " | " +
                                    isRecordChangesForNonActiveShows + " | " + isRecordChangesForSeasonsLowerThanCurrent + " | " +
                                    isStageMoveWithParentAndBlockParent + " | " + isEnableSpecialEffects + " | " + isFileLogging + " | " +
                                    showColumnWidth + " | " + remainingColumnWidth + " | " + seasonColumnWidth + " | " + episodeColumnWidth + " | " +
                                    showColumnVisibility + " | " + remainingColumnVisibility + " | " + seasonColumnVisibility + " | " + episodeColumnVisibility);
                            loadedProgramSettings = true;
                            break;
                        case "DIRECTORIES_START":
                            while (++i < settings.length)
                                ClassHandler.directoryController().addDirectory(new File(settings[i]));
                            break;
                        case "USERS_START":
                            while (i + 1 < settings.length) {
                                String user = settings[++i];
                                int videoPlayer = Integer.parseInt(settings[i]);
                                String videoPlayerFile = settings[++i];
                                if (loadedProgramSettings)
                                    ClassHandler.userInfoController().addUser(user, true, updateSpeed, enableAutomaticShowUpdating,
                                            timeToWaitForDirectory, show0Remaining, showActiveShows,
                                            language, isRecordChangesForNonActiveShows, isRecordChangesForSeasonsLowerThanCurrent,
                                            isStageMoveWithParentAndBlockParent, isStageMoveWithParentAndBlockParent,
                                            isEnableSpecialEffects, isFileLogging, showColumnWidth, remainingColumnWidth,
                                            seasonColumnWidth, episodeColumnWidth, showColumnVisibility, remainingColumnVisibility,
                                            seasonColumnVisibility, episodeColumnVisibility, videoPlayer, videoPlayerFile);
                                else ClassHandler.userInfoController().addUser(user);
                            }
                            break;
                        case "SHOW_START":
                            while (i < settings.length && settings[++i].matches("NEW_SHOW")) {
                                String showName = settings[++i];
                                boolean active = Boolean.valueOf(settings[++i]);
                                boolean hidden = Boolean.valueOf(settings[++i]);
                                boolean ignored = Boolean.valueOf(settings[++i]);
                                int currentSeason = Integer.parseInt(settings[++i]);
                                int currentEpisode = Integer.parseInt(settings[++i]);

                                log.info(showName + " | " + active + " | " + hidden + " | " + ignored + " | " + currentSeason + " | " + currentEpisode);
                            }
                            break;

                    }
                }
            }
            if (showRestartWindow && !firstRun && new ConfirmBox().confirm(Strings.DoYouWantToRestartTheProgramForTheImportToTakeFullEffectWarningSettingsChangedOutsideOfTheImportWontBeSaved, stage))
                Main.stop(stage, true);
            else new MessageBox(new StringProperty[]{Strings.MTrackHasNowImportedTheFiles}, stage);
        }
        log.info("importSettings has finished.");
        return result;
    }
}
