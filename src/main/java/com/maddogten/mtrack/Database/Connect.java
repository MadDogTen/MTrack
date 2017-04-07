package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.Strings;

public class Connect {


    private void setDBSystemDir() {
        String directory = "C:" + Strings.FileSeparator + "Test Folder" + Strings.FileSeparator + "DataBase";

        System.setProperty("derby.system.home", directory);
    }
}
