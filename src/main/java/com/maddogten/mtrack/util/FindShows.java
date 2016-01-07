package com.maddogten.mtrack.util;

import com.maddogten.mtrack.io.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
      FindShows looks for Shows, Their Seasons, and Their Episodes with the given information. It filters
      any files that don't match what it's looking for.

      Only Episode Extension's Currently accepted: ".mkv", ".avi", ".mp4", ".ts".
      Other extensions are supported, and just need to be added if needed.
 */

public class FindShows {
    private final Logger log = Logger.getLogger(FindShows.class.getName());

    public final ArrayList<String> findShows(File dir) {
        return new ArrayList<>(Arrays.asList(dir.list((dir1, name) -> new File(dir1 + Strings.FileSeparator + name).isDirectory())));
    }

    public final ArrayList<Integer> findSeasons(File dir, String show) {
        log.finest("Searching for seasons for: " + show + '.');
        ArrayList<String> showFolder = new ArrayList<>(Arrays.asList(new File(dir + Strings.FileSeparator + show).list((dir1, name) -> new File(dir1 + Strings.FileSeparator + name).isDirectory())));
        ArrayList<Integer> seasonNumber = new ArrayList<>(showFolder.size());
        Pattern pattern = Pattern.compile(Strings.seasonRegex);
        showFolder.forEach(aShowFolder -> {
            Matcher matcher = pattern.matcher(aShowFolder.toLowerCase());
            if (matcher.find()) seasonNumber.add(Integer.parseInt(matcher.group().toLowerCase().split(" ")[1]));
        });
        return seasonNumber;
    }

    public final ArrayList<String> findEpisodes(File dir, String ShowName, Integer season) {
        log.finest("Searching for episodes for: " + ShowName + " || Season: " + season + '.');
        File folder = new File(dir + Strings.FileSeparator + ShowName + Strings.FileSeparator + Strings.Season.getValue() + ' ' + season);
        if (new FileManager().checkFolderExistsAndReadable(folder) && new File(String.valueOf(folder)).list().length > 0)
            return new ArrayList<>(Arrays.asList(folder.list((dir1, name) -> {
                for (String extension : Variables.showExtensions)
                    if (new File(dir1 + Strings.FileSeparator + name).isFile() && name.toLowerCase().endsWith(extension)) {
                        return true;
                    }
                return false;
            })));
        return new ArrayList<>();
    }
}
