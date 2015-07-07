package program.io;

import program.util.Strings;
import program.util.Variables;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class FileManager {
    private final Logger log = Logger.getLogger(FileManager.class.getName());

    // Serialise
    public void save(Serializable objectToSerialise, String folder, String filename, String extension, boolean overWrite) {
        if (!new File(Variables.dataFolder + folder).isDirectory()) {
            createFolder(folder);
        }
        if (overWrite || !checkFileExists(folder, filename, extension)) {
            try {
                FileOutputStream fos = new FileOutputStream(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension);

                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(objectToSerialise);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                log.severe(e.toString());
            }
        } else log.info(filename + " save already exists.");
    }

    // Deserialize
    @SuppressWarnings("unchecked")
    public Object loadFile(String folder, String theFile, String extension) {
        Object loadedFile = new HashMap<>(0);
        if (checkFileExists(folder, theFile, extension)) {
            try {
                FileInputStream fis = new FileInputStream(new File(Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension));
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedFile = ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedFile;
        }
        log.info("File doesn't exist - " + (Variables.dataFolder + folder + Strings.FileSeparator + theFile + extension));
        return loadedFile;
    }

    public boolean checkFileExists(String folder, String filename, String extension) {
        return new File(Variables.dataFolder + folder + Strings.FileSeparator + filename + extension).isFile();
    }

    public boolean checkFolderExists(String aFolder) {
        return new File(aFolder).isDirectory();
    }

    @SuppressWarnings("AccessOfSystemProperties")
    private String getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return "windows";
        } else if (os.contains("mac")) {
            return "mac";
        } else if (os.contains("nix")) {
            return "nix";
        } else if (os.contains("nux")) {
            return "nux";
        } else if (os.contains("aix")) {
            return "aix";
        } else return "unknown";
    }

    public void createFolder(String folder) {
        if (!new File(Variables.dataFolder + folder).mkdir()) {
            log.warning("Cannot make: " + Variables.dataFolder + folder);
        }
        log.info("Created folder: " + folder);
    }

    @SuppressWarnings("AccessOfSystemProperties")
    public String getDataFolder() {
        String home = System.getProperty("user.home");
        String os = getOS();
        if (os.contains("windows")) {
            home = System.getenv("appdata");
        } else if (os.contains("mac")) {
            home += "~/Library/Application Support";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            home += "";
        }
        File dir = new File(home, Variables.ProgramRootFolder);
        return dir.getAbsolutePath();
    }

    public void deleteFile(String folder, String filename, String extension) {
        String file = (folder + Strings.FileSeparator + filename + extension);
        if (!checkFileExists(folder, filename, extension)) {
            log.warning("File " + Variables.dataFolder + file + " does not exist!");
        }
        File toDelete = new File(Variables.dataFolder + file);
        if (toDelete.canWrite()) {
            if (!toDelete.delete()) {
                log.warning("Cannot delete: " + toDelete);
            }
        } else log.warning("File " + Variables.dataFolder + file + " is write protected!");
    }

    public void deleteFolder(File toDeleteFolder) {
        if (!checkFolderExists(String.valueOf(toDeleteFolder))) {
            log.warning(toDeleteFolder + " does not exist!");
        }
        if (toDeleteFolder.canWrite()) {
            if (toDeleteFolder.list().length == 0) {
                if (!toDeleteFolder.delete()) {
                    log.warning("Cannot delete: " + toDeleteFolder);
                }
            } else {
                File[] files = toDeleteFolder.listFiles();
                if (files != null) {
                    for (File aFile : files) {
                        if (aFile.isFile()) {
                            if (!aFile.delete()) {
                                log.warning("Cannot delete: " + aFile);
                            }
                        }
                        if (aFile.isDirectory()) {
                            deleteFolder(aFile);
                        }
                        if (!toDeleteFolder.delete()) {
                            log.warning("Cannot delete: " + toDeleteFolder);
                        }
                    }
                }
            }
        } else log.warning(toDeleteFolder + " is write protected!");
    }

    public void open(File file) {
        String os = getOS();
        try {
            if (os.contains("windows")) {
                Runtime.getRuntime().exec(new String[]{
                        "rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()
                });
            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                Runtime.getRuntime().exec(new String[]{
                        "/usr/bin/open", file.getAbsolutePath()
                });
            } else {
                // Unknown Desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
                log.warning("FileManager- Your OS is Unknown, Attempting to open file anyways, But it may fail.");
            }
        } catch (IOException e) {
            log.severe(e.toString());
        }
    }
}
