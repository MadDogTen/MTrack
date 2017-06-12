package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Database.DBManager;
import com.maddogten.mtrack.Database.DBUpdateManager;

import java.sql.SQLException;
import java.util.logging.Logger;

public class UpdateManager {
    private final Logger log = Logger.getLogger(UpdateManager.class.getName());

    public void updateDatabase(DBManager dbManager) {
        try {
            new DBUpdateManager().updateDatabase(dbManager);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}