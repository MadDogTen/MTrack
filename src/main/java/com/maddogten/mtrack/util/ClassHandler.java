package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Database.DBManager;
import com.maddogten.mtrack.MainRun;
import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.io.CheckShowFiles;

public class ClassHandler {
    private static final DirectoryController directoryController = new DirectoryController();
    private static final ShowInfoController showInfoController = new ShowInfoController();
    private static final UserInfoController userInfoController = new UserInfoController();
    private static final ProgramSettingsController programSettingsController = new ProgramSettingsController();
    private static final CheckShowFiles checkShowFiles = new CheckShowFiles();
    private static final ChangeReporter changeReporter = new ChangeReporter();
    private static final DeveloperStuff developerStuff = new DeveloperStuff();
    private static final MainRun mainRun = new MainRun();
    private static DBManager DBManager = null;
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

    public static DBManager getDBManager() {
        if (DBManager == null)
            throw new IllegalStateException("DBManager was null, This shouldn't be called until after it was set.");
        return DBManager;
    }

    public static void setDBManager(final DBManager DBManager) {
        ClassHandler.DBManager = DBManager;
    }

    public static ChangeReporter changeReporter() {
        return changeReporter;
    }
}
