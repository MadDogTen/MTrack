package program.io;

import program.util.Variables;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class FileManager {
    private final Logger log = Logger.getLogger(FileManager.class.getName());

    // Serialise
    public void save(Serializable objectToSerialise, String folder, String filename, String extension, Boolean overWrite) {
        if (!new File(Variables.dataFolder + folder).isDirectory()) {
            createFolder(folder);
        }
        if (overWrite || !checkFileExists(folder, filename, extension)) {
            try {
                FileOutputStream fos = new FileOutputStream(Variables.dataFolder + folder + '\\' + filename + extension);

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
    public HashMap<String, HashMap<Integer, HashMap<String, String>>> loadShows(String theFile) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> loadedHashMap = new HashMap<>(0);
        if (checkFileExists(Variables.DirectoriesFolder, theFile, Variables.EmptyString)) {
            try {
                FileInputStream fis = new FileInputStream(new File(Variables.dataFolder + Variables.DirectoriesFolder + '\\' + theFile));
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, HashMap<Integer, HashMap<String, String>>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedHashMap;
        }
        log.info("File doesn't exist");
        return loadedHashMap;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, HashMap<String, HashMap<String, String>>> loadUserInfo(String folder, String filename, String extension) {
        HashMap<String, HashMap<String, HashMap<String, String>>> loadedHashMap = new HashMap<>();
        if (checkFileExists(folder, filename, extension)) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(Variables.dataFolder + folder + '\\' + filename + extension);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, HashMap<String, HashMap<String, String>>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedHashMap;
        }
        log.info("File doesn't exist");
        return loadedHashMap;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, ArrayList<String>> loadProgramSettings(String filename, String extension) {
        HashMap<String, ArrayList<String>> loadedHashMap = new HashMap<>();
        if (checkFileExists(Variables.EmptyString, filename, extension)) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(Variables.dataFolder + '\\' + filename + extension);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, ArrayList<String>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedHashMap;
        }
        log.info("File doesn't exist");
        return loadedHashMap;
    }

    public boolean checkFileExists(String folder, String filename, String extension) {
        return new File(Variables.dataFolder + folder + '\\' + filename + extension).isFile();
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
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            home = System.getenv("appdata");
        } else if (os.contains("mac")) {
            home += "~/Library/Application Support";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            home += "~/.";
        }
        File dir = new File(home, Variables.ProgramRootFolder);
        return dir.getAbsolutePath();
    }

    public void deleteFile(String folder, String filename, String extension) {
        String file = (folder + '\\' + filename + extension);
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
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{
                        "/usr/bin/open", file.getAbsolutePath()
                });
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
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
