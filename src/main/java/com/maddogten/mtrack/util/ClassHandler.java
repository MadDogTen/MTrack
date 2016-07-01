package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.MainRun;
import com.maddogten.mtrack.information.DirectoryController;
import com.maddogten.mtrack.information.ProgramSettingsController;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.io.CheckShowFiles;

public class ClassHandler {
    private static final DirectoryController directoryController = new DirectoryController();
    private static final ShowInfoController showInfoController = new ShowInfoController();
    private static final UserInfoController userInfoController = new UserInfoController();
    private static final ProgramSettingsController programSettingsController = new ProgramSettingsController();
    private static final CheckShowFiles checkShowFiles = new CheckShowFiles();
    private static final DeveloperStuff developerStuff = new DeveloperStuff();
    private static final MainRun mainRun = new MainRun();
    private static Controller controller = null;

    public static DirectoryController directoryController() {
        return directoryController;
    }

    public static ShowInfoController showInfoController() {
        return showInfoController;
    }

    public static UserInfoController userInfoController() {
        return userInfoController;
    }

    public static ProgramSettingsController programSettingsController() {
        return programSettingsController;
    }

    public static CheckShowFiles checkShowFiles() {
        return checkShowFiles;
    }

    public static DeveloperStuff developerStuff() {
        return developerStuff;
    }

    public static MainRun mainRun() {
        return mainRun;
    }

    public static Controller controller() {
        return controller;
    }

    public static void setController(final Controller controller) {
        if (ClassHandler.controller == null) ClassHandler.controller = controller;
    }
}
