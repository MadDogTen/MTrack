package program.util;

import program.information.ProgramSettingsController;
import program.information.UserInfoController;

import java.util.logging.Logger;

public class InnerVersionChecker {
    private static final Logger log = Logger.getLogger(InnerVersionChecker.class.getName());
    public int checkProgramSettingsFileVersion() {
        int version = ProgramSettingsController.getProgramSettingsVersion();
        if (Variables.ProgramSettingsFileVersion != version) {
            return version;
        } else return -1;
    }

    public int checkUserSettingsFileVersion() {
        int version = UserInfoController.getUserSettingsVersion();
        if (Variables.UserSettingsFileVersion != version) {
            return version;
        } else return -1;
    }
}
