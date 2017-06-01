package com.maddogten.mtrack.io;

import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.gui.MultiChoice;
import com.maddogten.mtrack.gui.TextBox;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.OperatingSystem;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
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
        /*log.info("importSettings has been started.");
        boolean result = false, showRestartWindow = true;
        File importFile = new TextBox().pickFile(Strings.EnterFileLocation, new SimpleStringProperty(Strings.EmptyString), new StringProperty[]{new SimpleStringProperty("MTrack (*.MTrack)")}, new String[]{".MTrack"}, false, stage);
        if (importFile.toString().isEmpty()) log.info("importFile was empty, Nothing imported.");
        else {
            // Start of custom code for personal use only.
            if (!firstRun && importFile.getName().endsWith(".xml")) {
                log.info("XML importing has started.");
                try {
                    NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(importFile).getElementsByTagName("Series");
                    Set<String> shows = new HashSet<>();
                    for (int x = 0; x < nodeList.getLength(); x++) {
                        Node node = nodeList.item(x);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String show = element.getElementsByTagName("SeriesName").item(0).getTextContent();
                            shows.add(show);
                            log.info("Found Show in XML: \"" + show + "\".");
                            int season = Integer.parseInt(element.getElementsByTagName("Season").item(0).getTextContent()), episode = Integer.parseInt(element.getElementsByTagName("Episode").item(0).getTextContent()) + 1;
                            UserSettings userSettings = ClassHandler.userInfoController().getUserSettings();
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
                log.info("XML importing is now finished.");
            } // end of custom code for personal use only.
            else {
                try {
                    byte[] buffer = new byte[2048];
                    try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(importFile))) {
                        ZipEntry zipEntry = zipInputStream.getNextEntry();
                        boolean autoOverwrite = firstRun || new ConfirmBox().confirm(Strings.AutomaticallyOverwriteFilesIfNoYouWillBeAskedForEachExistingFile, stage);
                        while (zipEntry != null) {
                            String entryName = zipEntry.getName();
                            File file = new File(Variables.dataFolder + Strings.FileSeparator + entryName);
                            log.info("Next file to be imported: \"" + file + '\"');
                            if (!file.exists() || (autoOverwrite || new ConfirmBox().confirm(new SimpleStringProperty(Strings.Warning.getValue() + " \"" + entryName + "\" " + Strings.AlreadyExistsOverwriteIt.getValue()), stage))) {
                                if (zipEntry.isDirectory()) log.info(file + " is a directory.");
                                else {
                                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                                        int count;
                                        while ((count = zipInputStream.read(buffer)) > 0)
                                            fileOutputStream.write(buffer, 0, count);
                                    }
                                }
                                log.info("File imported successfully.");
                            } else log.info("File wasn't imported.");
                            zipInputStream.closeEntry();
                            zipEntry = zipInputStream.getNextEntry();
                        }
                        zipInputStream.closeEntry();
                    }
                    result = true;
                } catch (Exception e) {
                    GenericMethods.printStackTrace(log, e, FileManager.class);
                }
            }
            if (showRestartWindow && !firstRun && new ConfirmBox().confirm(Strings.DoYouWantToRestartTheProgramForTheImportToTakeFullEffectWarningSettingsChangedOutsideOfTheImportWontBeSaved, stage))
                Main.stop(stage, true, false);
            else new MessageBox(new StringProperty[]{Strings.MTrackHasNowImportedTheFiles}, stage);
        }
        log.info("importSettings has finished.");
        return result;*/
        return false; // TODO FIX
    }
}
