package com.maddogten.mtrack.io;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.OperatingSystem;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("AccessOfSystemProperties")
    public File getAppDataFolder() {
        String home = System.getProperty("user.home");
        OperatingSystem os = getOS();
        switch (os) {
            case WINDOWS:
                home = System.getenv("appdata");
                break;
            case MAC:
                home += "~/Library/Application Support";
                break;
            case NIX:
            case NUX:
            case AIX:
                home += Strings.EmptyString;
                break;
        }
        File dir = new File(home, Variables.ProgramRootFolder);
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

    public void deleteFile(String folder, String filename, String extension) {
        if (checkFileExists(folder, filename, extension)) {
            File toDelete = new File(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension);
            if (toDelete.canWrite() && toDelete.delete() && !toDelete.exists())
                log.info("\"" + toDelete + "\" was successfully deleted.");
            else log.warning("Cannot delete: " + toDelete);
        } else
            log.warning("File " + Variables.dataFolder + folder + Strings.FileSeparator + filename + extension + " does not exist!");
    }

    public void deleteFolder(File toDeleteFolder) {
        if (!checkFolderExistsAndReadable(toDeleteFolder)) log.warning(toDeleteFolder + " does not exist!");
        if (toDeleteFolder.canWrite()) {
            if (toDeleteFolder.list().length == 0) {
                if (!toDeleteFolder.delete()) log.warning("Cannot delete: " + toDeleteFolder);
            } else {
                File[] files = toDeleteFolder.listFiles();
                if (files != null) {
                    for (File aFile : files) {
                        if (!aFile.isFile() || !aFile.delete()) log.warning("Cannot delete: " + aFile);
                        if (aFile.isDirectory()) deleteFolder(aFile);
                    }
                }
                if (!toDeleteFolder.delete()) log.warning("Cannot delete: " + toDeleteFolder);
            }
        } else log.warning(toDeleteFolder + " is write protected!");
    }

    public void open(File file) {
        OperatingSystem os = getOS();
        try {
            if (os == OperatingSystem.WINDOWS) {
                Runtime.getRuntime().exec(new String[]{
                        "rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()
                });
            } else if (os == OperatingSystem.MAC || os == OperatingSystem.NIX || os == OperatingSystem.NUX || os == OperatingSystem.AIX) {
                Runtime.getRuntime().exec(new String[]{
                        "/usr/bin/open", file.getAbsolutePath()
                });
            } else {
                // Unknown Desktop
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
                log.warning("FileManager- Your OS is Unknown, Attempting to open file anyways, But it may fail.");
            }
        } catch (IOException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}