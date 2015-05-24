package program.util;

import program.information.ProgramSettingsController;
import program.information.UserInfoController;

import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class InnerVersionChecker {
    private static final Logger log = Logger.getLogger(InnerVersionChecker.class.getName());
    public int checkProgramSettingsFileVersion() {
        log.finest("checkProgramSettingsFileVersion has been ran.");
        int version = ProgramSettingsController.getProgramSettingsVersion();
        if (Variables.ProgramSettingsFileVersion != version) {
            return version;
        } else return -1;
    }

    public int checkUserSettingsFileVersion() {
        log.finest("checkUserSettingsFileVersion has been ran.");
        int version = UserInfoController.getUserSettingsVersion();
        if (Variables.UserSettingsFileVersion != version) {
            return version;
        } else return -1;
    }
}
