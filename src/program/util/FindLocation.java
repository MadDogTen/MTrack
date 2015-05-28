package program.util;

import program.io.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindLocation {
    private static final Logger log = Logger.getLogger(FindLocation.class.getName());

    public static ArrayList<String> findShows(File dir) {
        ArrayList<String> result = new ArrayList<>();
        Collections.addAll(result, dir.list());
        return result;
    }

    public static ArrayList<Integer> findSeasons(File dir, String show) {
        log.finest("Searching for seasons...");
        File folder = new File(dir + Strings.FileSeparator + show);
        ArrayList<String> showFolder = new ArrayList<>();
        Collections.addAll(showFolder, folder.list((dir1, name) -> new File(dir1, name).isDirectory()));
        ArrayList<Integer> seasonNumber = new ArrayList<>();
        Pattern pattern = Pattern.compile("[s][e][a][s][o][n]\\s\\d{1,4}");
        showFolder.forEach(aShowFolder -> {
            Matcher matcher = pattern.matcher(aShowFolder.toLowerCase());
            if (matcher.find()) {
                String matched = matcher.group();
                String[] seasonsList = matched.toLowerCase().split(" ");
                seasonNumber.add(Integer.parseInt(seasonsList[1]));
            }
        });
        log.finest("Finished searching for seasons.");
        return seasonNumber;
    }

    public static ArrayList<String> findEpisodes(File dir, String ShowName, Integer Season) {
        log.finest("Searching for episodes...");
        ArrayList<String> episodes = new ArrayList<>();
        File folder = new File(dir + Strings.FileSeparator + ShowName + "/Season" + ' ' + Season);
        if (new FileManager().checkFolderExists(String.valueOf(folder)) && new File(String.valueOf(folder)).list().length > 0) {
            Collections.addAll(episodes, folder.list((dir1, name) -> name.toLowerCase().endsWith(".mkv") || name.toLowerCase().endsWith(".avi") || name.toLowerCase().endsWith(".mp4") || name.toLowerCase().endsWith(".ts")));
            return episodes;
        }
        log.finest("Finished searching for episodes.");
        return episodes;
    }
}