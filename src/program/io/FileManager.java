package program.io;

import program.util.Variables;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class FileManager {
    private static final Logger log = Logger.getLogger(FileManager.class.getName());

    // Serialise
    public static void save(Serializable objectToSerialise, String folder, String filename, String extension, Boolean overWrite) {
        if (!folder.isEmpty()) {
            createFolder(folder);
        }
        if (overWrite || !checkFileExists(folder, filename, extension)) {
            try {
                FileOutputStream fos = new FileOutputStream(getDataFolder() + folder + '\\' + filename + extension);

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
    public static HashMap loadShows(String theFile) {
        if (checkFileExists(Variables.DirectoriesFolder, theFile, Variables.EmptyString)) {
            HashMap<String, HashMap<Integer, HashMap<String, String>>> loadedHashMap = new HashMap<String, HashMap<Integer, HashMap<String, String>>>(0);
            try {
                FileInputStream fis = new FileInputStream(new File(getDataFolder() + Variables.DirectoriesFolder + '\\' + theFile));
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, HashMap<Integer, HashMap<String, String>>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedHashMap;
        }
        log.info("File doesn't exist");
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, HashMap<String, String[]>> loadUserInfo(String folder, String filename, String extension) {
        if (checkFileExists(folder, filename, extension)) {
            HashMap<String, HashMap<String, String[]>> loadedHashMap = new HashMap<>();
            FileInputStream fis;
            try {
                fis = new FileInputStream(getDataFolder() + folder + '\\' + filename + extension);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, HashMap<String, String[]>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedHashMap;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, ArrayList<String>> loadProgramSettings(String filename, String extension) {
        if (checkFileExists(Variables.EmptyString, filename, extension)) {
            HashMap<String, ArrayList<String>> loadedHashMap = new HashMap<>();
            FileInputStream fis;
            try {
                fis = new FileInputStream(getDataFolder() + '\\' + filename + extension);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, ArrayList<String>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                log.severe(e.toString());
            }
            return loadedHashMap;
        }
        return null;
    }

    public static boolean checkFileExists(String folder, String filename, String extension) {
        return new File(getDataFolder() + folder + '\\' + filename + extension).isFile();
    }

    public static boolean checkFolderExists(String aFolder) {
        return new File(aFolder).isDirectory();
    }

    public static boolean checkFileExists(File aFile) {
        return aFile.isFile();
    }

    public static String getOS() {
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

    private static void createFolder(String folder) {
        new File(getDataFolder() + folder).mkdir();
        log.info("Created Data Folder!");
    }

    public static void createBaseFolder() {
        createFolder(Variables.EmptyString);
    }

    public static String getDataFolder() {
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

    public static void deleteFile(String folder, String filename, String extension) {
        String file = (folder + '\\' + filename + extension);
        if (!checkFileExists(folder, filename, extension)) {
            log.warning("File " + getDataFolder() + file + " does not exist!");
        }
        File toDelete = new File(getDataFolder() + file);

        if (toDelete.canWrite()) {
            toDelete.delete();
        } else log.warning("File " + getDataFolder() + file + " is write protected!");
    }

    public static void deleteFolder(File toDeleteFolder) {
        if (!checkFolderExists(String.valueOf(toDeleteFolder))) {
            log.warning(toDeleteFolder + " does not exist!");
        }
        if (toDeleteFolder.canWrite()) {
            if (toDeleteFolder.list().length == 0) {
                toDeleteFolder.delete();
            } else {
                File[] files = toDeleteFolder.listFiles();
                for (File aFile : files) {
                    if (aFile.isFile()) {
                        aFile.delete();
                    }
                    if (aFile.isDirectory()) {
                        deleteFolder(aFile);
                    }
                    toDeleteFolder.delete();
                }
            }
        } else log.warning(toDeleteFolder + " is write protected!");
    }

    public static void open(File file) {
        String os = FileManager.getOS();
        try {
            if (os.contains("windows")) {
                /*Runtime.getRuntime().exec(new String[]{
                        "rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()
                });*/
                log.info("File Played!"); // --------------------------------------------------------- Temp
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
                log.warning("FileManager- Your OS is Unknown, Attempting to open file, But it may fail.");
            }
        } catch (IOException e) {
            log.severe(e.toString());
        }
    }
}
