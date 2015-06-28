package program.util;

import program.information.ProgramSettingsController;
import program.io.FileManager;

@SuppressWarnings("ClassWithoutLogger")
public class Variables {
    //---------- Main Variables ----------\\
    public static final int SIZE_WIDTH = 282;
    public static final int SIZE_HEIGHT = 454;
    // Base Directories
    public static final String ProgramRootFolder = "/MTrack";
    public static final String DirectoriesFolder = "/Directories";
    public static final String UsersFolder = "/Users";
    // Extensions
    public static final String ShowsExtension = ".shows";
    public static final String UsersExtension = ".user";
    public static final String SettingsExtension = ".settings";
    // Inner Version Numbers \\ // Set to 1000+ for Pre-Alpha / Alpha / Beta -- Set back to 1 for release. \\
    public static final int ProgramSettingsFileVersion = 1005;
    public static final int UserSettingsFileVersion = 1001;

    public static final Boolean devMode = false;
    public static final Boolean StartFresh = false; // Won't work unless devMode is true.

    public static final Integer defaultUpdateSpeed = 12;
    public static final String Logo = "/image/MTrackLogo.png";
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