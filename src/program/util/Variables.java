package program.util;

import program.information.ProgramSettingsController;

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
    // Inner Version Numbers
    public static final int ProgramSettingsFileVersion = 2; //TODO Reset back to 1 for release 0.9
    public static final int UserSettingsFileVersion = 2;
    // Other
    public static final String EmptyString = "";
    public static final Boolean StartFresh = false;
    private static final Logger log = Logger.getLogger(Variables.class.getName());
    public static Integer updateSpeed;


    //---------- Temp Variables ----------\\

    public static void setUpdateSpeed() {
        updateSpeed = ProgramSettingsController.getUpdateSpeed();
    }
}