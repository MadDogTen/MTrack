package program.util;

import program.information.ProgramSettingsController;
import program.information.UserInfoController;

public class InnerVersionChecker {
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
