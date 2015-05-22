package program.util;

import program.information.ProgramSettingsController;
import program.io.FileManager;

import java.util.logging.Logger;

public class Variables {
    //---------- Main Variables ----------\\
    public static final int SIZE_WIDTH = 282;
    public static final int SIZE_HEIGHT = 454;
    // Base Directories
    public static final String ProgramRootFolder = "\\MTrack";
    public static final String DirectoriesFolder = "\\Directories";
    public static final String UsersFolder = "\\Users";
    // Extensions
    public static final String ShowsExtension = ".shows";
    public static final String UsersExtension = ".user";
    public static final String SettingsExtension = ".settings";
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- Set back to 1 for release. \\
    public static final int ProgramSettingsFileVersion = 1003;
    public static final int UserSettingsFileVersion = 1000;
    // Other
    public static final String EmptyString = "";

    public static final Boolean devMode = false;
    public static final Boolean StartFresh = false; // Won't work unless devMode is true.

    private static final Logger log = Logger.getLogger(Variables.class.getName());
    public static Integer updateSpeed;
    public static String dataFolder;

    //---------- Other Variables ----------\\

    public static void setUpdateSpeed() {
        updateSpeed = ProgramSettingsController.getUpdateSpeed();
    }

    public static void setDataFolder(FileManager fileManager) {
        dataFolder = fileManager.getDataFolder();
    }

    //---------- Temp Variables ----------\\
}