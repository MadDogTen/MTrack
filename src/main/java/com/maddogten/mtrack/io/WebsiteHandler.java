package com.maddogten.mtrack.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class WebsiteHandler {
    @SuppressWarnings("FieldCanBeLocal")
    private final Logger log = Logger.getLogger(WebsiteHandler.class.getName());

    private final URL url;
    private final String websiteData;

    public WebsiteHandler(final String string) throws IOException {
        if (/*!ClassHandler.userInfoController().useOnlineDatabase(Variables.currentUser)*/false) {
            this.url = new URL("");
            this.websiteData = "";
        } else {
            log.info(string);
            this.url = new URL(string);
            websiteData = readDataFromWebsite();
        }
    }

    private String readDataFromWebsite() throws IOException {
        String data;
        try (BufferedReader bufferedReaderIn = new BufferedReader(new InputStreamReader(url.openStream()))) {
            data = bufferedReaderIn.readLine();
        }
        return data;
    }

    public String getWebsiteData() {
        return websiteData;
    }
}
