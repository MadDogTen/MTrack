package com.maddogten.mtrack.io;

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

    public void save(Serializable objectToSerialise, String folder, String filename, String extension, boolean overWrite) {
        if (!new File(Variables.dataFolder + folder).isDirectory()) createFolder(folder);
        if (overWrite || !checkFileExists(folder, filename, extension)) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension));
                oos.writeObject(objectToSerialise);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        } else log.info(filename + " save already exists.");
    }

    public Object loadFile(String folder, String theFile, String extension) {
        if (checkFileExists(folder, theFile, extension)) {
            Object loadedFile = null;
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension)));
                loadedFile = ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
            return loadedFile;
        }
        log.info("File doesn't exist - " + (Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension));
        //noinspection ReturnOfNull
        return null;
    }

    public boolean checkFileExists(String folder, String filename, String extension) {
        return new File(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension).isFile();
    }

    public boolean checkFolderExistsAndReadable(File aFolder) {
        return aFolder.isDirectory() && aFolder.canRead();
    }

    // Detects the operating system that the program is current on. 
    @SuppressWarnings("AccessOfSystemProperties")
    private OperatingSystem getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) return OperatingSystem.WINDOWS;
        else if (os.contains("mac")) return OperatingSystem.MAC;
        else if (os.contains("nix")) return OperatingSystem.NIX;
        else if (os.contains("nux")) return OperatingSystem.NUX;
        else if (os.contains("aix")) return OperatingSystem.AIX;
        else {
            log.warning("Your operating system is unknown, Assuming linux based, Using nix...");
            return OperatingSystem.NIX;
        }
    }

    public void createFolder(String folder) {
        if (!new File(Variables.dataFolder + folder).mkdir())
            log.warning("Cannot make: " + Variables.dataFolder + folder);
        log.info("Created folder: " + folder);
    }

    // Using the above getOS(), Determines where the program files should be saved.
    @SuppressWarnings("AccessOfSystemProperties")
    public File findProgramFolder() {
        String home = System.getProperty("user.home");
        OperatingSystem os = getOS();
        switch (os) {
            case WINDOWS:
                home = System.getenv("appdata");
                break;
            case MAC:
                home += "~/Library/Preferences";
                break;
            case NIX:
            case NUX:
            case AIX:
                home += Strings.EmptyString;
                break;
        }
        File dir = new File(home + Variables.ProgramRootFolder);
        log.info("Appdata folder: \"" + dir.getAbsolutePath() + "\".");
        return new File(dir.getAbsolutePath());
    }

    public File getJarLocationFolder() throws UnsupportedEncodingException {
        File file = new File(URLDecoder.decode(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
        String converted = Strings.EmptyString;
        String[] split = String.valueOf(file).split(Pattern.quote(Strings.FileSeparator));
        for (String splitPart : split) {
            if (converted.isEmpty()) converted = splitPart;
            else converted += Strings.FileSeparator + splitPart;
        }
        return new File(converted);
    }

    // This is to make sure it doesn't delete files other than ones generated by this program if they are stored in jar location (For example, On the desktop/in the downloads folder).
    public void clearProgramFiles() {
        if (Variables.dataFolder.toString().matches(Pattern.quote(String.valueOf(findProgramFolder())))) {
            log.info("Deleting " + Variables.dataFolder + " in AppData...");
            deleteFolder(Variables.dataFolder);
        } else {
            log.info("Deleting appropriate files found in the folder the jar is contained in...");
            File[] files = Variables.dataFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean valid = false;
                    if (file.toString().endsWith(Variables.SettingFileExtension)) valid = true;
                    else {
                        String[] splitFile = file.toString().split(Pattern.quote(Strings.FileSeparator));
                        String directory = Strings.FileSeparator + splitFile[splitFile.length - 1];
                        if (directory.matches(Pattern.quote(Variables.DirectoriesFolder)) || directory.matches(Pattern.quote(Variables.UsersFolder)))
                            valid = true;
                    }
                    if (valid) {
                        if (file.isDirectory()) deleteFolder(file);
                        else deleteFile(file);
                    }
                }
            }
        }
    }

    private boolean deleteFile(File file) {
        if (file.exists() && file.isFile()) {
            if (file.canWrite() && file.delete() && !file.exists()) {
                log.info("\"" + file + "\" was successfully deleted.");
                return true;
            } else log.warning("Cannot delete: " + file);
        } else log.info("File " + file + " does not exist!");
        return false;
    }

    public boolean deleteFile(String folder, String filename, String extension) {
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

    private void deleteFolder(File toDeleteFolder) {
        if (!checkFolderExistsAndReadable(toDeleteFolder)) log.warning(toDeleteFolder + " does not exist!");
        if (toDeleteFolder.canWrite()) {
            if (toDeleteFolder.list().length == 0) {
                if (!toDeleteFolder.delete()) log.warning("Cannot delete: " + toDeleteFolder);
            } else {
                File[] files = toDeleteFolder.listFiles();
                if (files != null) {
                    for (File aFile : files) {
                        if (aFile.isFile() && deleteFile(aFile)) log.info(aFile + " was deleted.");
                        else if (aFile.isDirectory()) deleteFolder(aFile);
                    }
                }
                if (!toDeleteFolder.delete()) log.warning("Cannot delete: " + toDeleteFolder);
            }
        } else log.warning(toDeleteFolder + " is write protected!");
    }

    public void open(File file) {
        OperatingSystem os = getOS();
        try {
            if (os == OperatingSystem.WINDOWS)
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()});
            else if (os == OperatingSystem.MAC || os == OperatingSystem.NIX || os == OperatingSystem.NUX || os == OperatingSystem.AIX)
                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", file.getAbsolutePath()});
            else {
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
                log.warning("FileManager- Your OS is Unknown, Attempting to open file anyways...");
            }
        } catch (IOException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void exportSettings(Stage stage) {
        log.info("exportSettings has been started.");
        ArrayList<String> choices = new MultiChoice().multipleCheckbox(new StringProperty[]{Strings.ChooseWhatToExport}, new StringProperty[]{Strings.All, Strings.Program, Strings.Users, Strings.Directories}, null, Strings.All, false, stage);
        if (choices.isEmpty()) log.info("No choices were selected, Noting exported");
        else {
            byte[] buffer = new byte[1024];
            try {
                File file = new TextBox().pickFile(new SimpleStringProperty("Enter Filename"), new SimpleStringProperty("MTrackExport"), new SimpleStringProperty("Zip files (*.zip)"), new String[]{"*.zip"}, stage);// TODO Do this in a better, non-lazy way.
                log.info("Directory to save export in: \"" + file + "\'.");
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
                ZipEntry zipEntry = new ZipEntry("MTrackSettings");
                zipOutputStream.putNextEntry(zipEntry);
                ArrayList<FileInputStream> fileInputStreams = new ArrayList<>();

                if (choices.contains(Strings.All.getValue()) || choices.contains(Strings.Program.getValue()))
                    fileInputStreams.add(new FileInputStream(Variables.dataFolder + Strings.FileSeparator + Strings.SettingsFileName + Variables.SettingFileExtension));
                if (choices.contains(Strings.All.getValue()) || choices.contains(Strings.Directories.getValue())) {
                    Arrays.asList(new File(Variables.dataFolder + Strings.FileSeparator + Variables.DirectoriesFolder).list()).forEach(aFile -> {
                        if (aFile.endsWith(Variables.ShowFileExtension)) {
                            try {
                                fileInputStreams.add(new FileInputStream(Variables.dataFolder + Strings.FileSeparator + Variables.DirectoriesFolder + Strings.FileSeparator + aFile));
                            } catch (FileNotFoundException e) {
                                GenericMethods.printStackTrace(log, e, FileManager.class);
                            }
                        }
                    });
                }
                if (choices.contains(Strings.All.getValue()) || choices.contains(Strings.Users.getValue())) {
                    Arrays.asList(new File(Variables.dataFolder + Strings.FileSeparator + Variables.UsersFolder).list()).forEach(aFile -> {
                        if (aFile.endsWith(Variables.UserFileExtension)) {
                            try {
                                fileInputStreams.add(new FileInputStream(Variables.dataFolder + Strings.FileSeparator + Variables.UsersFolder + Strings.FileSeparator + aFile));
                            } catch (FileNotFoundException e) {
                                GenericMethods.printStackTrace(log, e, FileManager.class);
                            }
                        }
                    });
                }
                fileInputStreams.forEach(fileInputStream -> {
                    int len;
                    try {
                        while ((len = fileInputStream.read(buffer)) > 0) zipOutputStream.write(buffer, 0, len);
                        fileInputStream.close();
                    } catch (IOException e) {
                        GenericMethods.printStackTrace(log, e, FileManager.class);
                    }
                });
                zipOutputStream.closeEntry();
                zipOutputStream.close();
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, FileManager.class);
            }
        }
        log.info("exportSettings has finished.");
    }

    /*public void importSettings() {
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(""));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
            }
        } catch (IOException e) {
            GenericMethods.printStackTrace(log, e, FileManager.class);
        }
    }*/
}
