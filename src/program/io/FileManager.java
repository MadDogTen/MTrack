package program.io;

import program.util.Variables;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {
    // Serialise
    public static void save(Serializable objectToSerialise, String folder, String filename, String extension, Boolean overWrite) {
        FileOutputStream fos = null;
        String file = (folder + '\\' + filename + extension);
        if (!folder.isEmpty()) {
            createSubFolder(folder);
        }
        if (overWrite || !checkFileExists(folder, filename, extension)) {
            try {
                fos = new FileOutputStream(createDataFolder() + file);

                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(objectToSerialise);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println(filename + " save already exists.");
    }

    // Deserialize
    @SuppressWarnings("unchecked")
    public static HashMap loadShows(String theFile) {
        String file = ('\\' + theFile);
        if (checkFileExists("", theFile, "")) {
            HashMap<String, HashMap<Integer, HashMap<String, String>>> loadedHashMap = new HashMap<String, HashMap<Integer, HashMap<String, String>>>(0);
            FileInputStream fis;
            try {
                fis = new FileInputStream(createDataFolder() + file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, HashMap<Integer, HashMap<String, String>>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            return loadedHashMap;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<HashMap<String, String>> loadSeasonEpisode(String filename, String extension) {
        String file = ('\\' + filename + extension);
        if (checkFileExists("", filename, extension)) {
            ArrayList<HashMap<String, String>> loadedArrayList = new ArrayList<>();
            FileInputStream fis;
            try {
                fis = new FileInputStream(createDataFolder() + file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedArrayList = (ArrayList<HashMap<String, String>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            return loadedArrayList;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, HashMap<String, String[]>> loadUserInfo(String folder, String filename, String extension) {
        String file = (folder + '\\' + filename + extension);
        if (checkFileExists(folder, filename, extension)) {
            HashMap<String, HashMap<String, String[]>> loadedHashMap = new HashMap<>();
            FileInputStream fis;
            try {
                fis = new FileInputStream(getDataFolder() + file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, HashMap<String, String[]>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            return loadedHashMap;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, ArrayList<String>> loadProgramSettings(String folder, String filename, String extension) {
        String file = (folder + '\\' + filename + extension);
        if (checkFileExists(folder, filename, extension)) {
            HashMap<String, ArrayList<String>> loadedHashMap = new HashMap<>();
            FileInputStream fis;
            try {
                fis = new FileInputStream(getDataFolder() + file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loadedHashMap = (HashMap<String, ArrayList<String>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            return loadedHashMap;
        }
        return null;
    }

    public static boolean checkFileExists(String folder, String filename, String extension) {
        String file = (folder + '\\' + filename + extension);
        return new File(createDataFolder() + file).isFile();
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

    private static String createDataFolder() {
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            home = System.getenv("appdata");
        } else if (os.contains("mac")) {
            home += "~/Library/Application Support";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            home += "~/.";
        }

        File dir = new File(home, Variables.programRootFolder);

        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("Created Data Folder!");
        }

        return dir.getAbsolutePath();
    }

    private static void createSubFolder(String folder) {
        File dir = new File(createDataFolder() + folder);

        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("Created Settings Folder!");
        }
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

        File dir = new File(home, Variables.programRootFolder);

        return dir.getAbsolutePath();
    }

    public static void deleteFile(String folder, String filename, String extension) {
        String file = (folder + '\\' + filename + extension);
        if (!checkFileExists(folder, filename, extension)) {
            System.err.println("File " + createDataFolder() + file + " does not exist!");
        }
        File toDelete = new File(createDataFolder() + file);

        if (toDelete.canWrite()) {
            toDelete.delete();
        } else System.err.println("File " + createDataFolder() + file + " is write protected!");
    }

    public static void deleteFolder(File toDeleteFolder) {
        if (!checkFolderExists(String.valueOf(toDeleteFolder))) {
            System.err.println(toDeleteFolder + " does not exist!");
        }

        System.out.println(toDeleteFolder);
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
        } else System.err.println(toDeleteFolder + " is write protected!");
    }

    public static void open(File file) {
        String os = FileManager.getOS();
        try {
            if (os.contains("windows")) {
                Runtime.getRuntime().exec(new String[]{
                        "rundll32", "url.dll,FileProtocolHandler", file.getAbsolutePath()
                });
                //System.out.println("File Played!"); // --------------------------------------------------------- Temp
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
                System.out.println("Your OS is Unknown, Attempting to open file, But it may fail.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
