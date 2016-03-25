package com.maddogten.mtrack.util;

import com.maddogten.mtrack.io.WebsiteHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetShowInfo {
    private String convertShowName(String show) {
        return show.replaceAll("\\s\\(\\d{4}\\)", "").replaceAll("\\s\\(\\S\\S\\)", "");
    }

    public int getShowID(String aShow) throws IOException {
        Matcher matcher = Pattern.compile(".\"id\":(\\d{1,6}).*\"url\":").matcher(new WebsiteHandler(Variables.findShowURL + convertShowName(aShow)).getWebsiteData());
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    public HashMap<Integer, Integer> getShowInfo(int showID) throws IOException {
        WebsiteHandler websiteHandler = new WebsiteHandler(Variables.getShowWithID + showID + Variables.episodesAddition);
        Pattern pattern = Pattern.compile("\"season\":(\\d{1,3}),\"number\":(\\d{1,3})");
        Matcher matcher = pattern.matcher(websiteHandler.getWebsiteData());
        HashMap<Integer, Integer> showData = new HashMap<>();
        while (matcher.find()) {
            String showData1 = matcher.group();
            Matcher matcher1 = pattern.matcher(showData1);
            if (matcher1.find()) {
                int season = Integer.parseInt(matcher1.group(1));
                if (!showData.containsKey(season)) showData.put(season, -1);
                int episode = Integer.parseInt(matcher1.group(2));
                if (showData.get(season) == -1 || showData.get(season) < episode)
                    showData.replace(season, episode);
            }
        }
        return showData;
    }
}
