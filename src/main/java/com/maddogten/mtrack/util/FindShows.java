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
      any files that doesn't match what it's looking for.

      Only Video Extension's Currently supported: ".mkv", ".avi", ".mp4", ".ts".
      Other extensions can be added on a as needed bases.
 */

public class FindShows {
    private final Logger log = Logger.getLogger(FindShows.class.getName());

    public final ArrayList<String> findShows(final File dir) {
        return new ArrayList<>(Arrays.asList(dir.list((dir1, name) -> new File(dir1 + Strings.FileSeparator + name).isDirectory())));
    }

    public final ArrayList<Integer> findSeasons(final File dir, final String show) {
        log.finest("Searching for seasons for: " + show + '.');
        ArrayList<String> showFolder = new ArrayList<>();
        if (new File(dir + Strings.FileSeparator + show).list() != null) {
            showFolder.addAll(Arrays.asList(new File(dir + Strings.FileSeparator + show).list((dir1, name) -> new File(dir1 + Strings.FileSeparator + name).isDirectory())));
            ArrayList<Integer> seasonNumber = new ArrayList<>(showFolder.size());
            Pattern pattern = Pattern.compile(Strings.seasonRegex + "\\s" + Strings.seasonNumberRegex);
            Pattern pattern1 = Pattern.compile("s" + Strings.seasonNumberRegex);
            showFolder.forEach(aShowFolder -> {
                Matcher matcher = pattern.matcher(aShowFolder.toLowerCase());
                if (matcher.find()) seasonNumber.add(Integer.parseInt(matcher.group().toLowerCase().split(" ")[1]));
                else {
                    matcher = pattern1.matcher(aShowFolder.toLowerCase());
                    if (matcher.find())
                        seasonNumber.add(Integer.parseInt(matcher.group().toLowerCase().replace("s", "")));
                }
            });
            return seasonNumber;
        } else log.fine("Folder for " + show + " was found to be null.");
        return new ArrayList<>();
    }

    public final ArrayList<String> findEpisodes(final File dir, final String showName, final int season) {
        log.finest("Searching for episodes for: " + showName + " || Season: " + season + '.');
        String seasonFolder = GenericMethods.getSeasonFolderName(dir, showName, season);
        if (!seasonFolder.isEmpty()) {
            File folder = new File(dir + Strings.FileSeparator + showName + Strings.FileSeparator + seasonFolder);
            if (folder.exists() && new FileManager().checkFolderExistsAndReadable(folder) && new File(String.valueOf(folder)).list().length > 0)
                return new ArrayList<>(Arrays.asList(folder.list((dir1, name) -> {
                    for (String extension : Variables.showExtensions)
                        if (new File(dir1 + Strings.FileSeparator + name).isFile() && name.toLowerCase().endsWith(extension) && ClassHandler.showInfoController().getEpisodeInfo(name)[0] != -2)
                            return true;
                    return false;
                })));
        }
        return new ArrayList<>(0);
    }
}
