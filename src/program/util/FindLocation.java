package program.util;

import program.io.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindLocation {
    private static final Logger log = Logger.getLogger(FindLocation.class.getName());

    public static String[] findShows(File dir) {
        return dir.list();
    }

    public static ArrayList<Integer> findSeasons(File dir, String show) {
        File folder = new File(dir + "\\" + show);
        String[] seasonsList, showFolder = folder.list((dir1, name) -> new File(dir1, name).isDirectory());
        ArrayList<Integer> seasonNumber = new ArrayList<>();
        if (showFolder != null) {
            Pattern pattern = Pattern.compile("[s][e][a][s][o][n]\\s\\d{1,4}");
            for (String aShowFolder : showFolder) {
                Matcher matcher = pattern.matcher(aShowFolder.toLowerCase());
                if (matcher.find()) {
                    String matched = matcher.group();
                    seasonsList = matched.toLowerCase().split(" ");
                    seasonNumber.add(Integer.parseInt(seasonsList[1]));
                }
            }
        }
        return seasonNumber;
    }

    public static String[] findEpisodes(File dir, String ShowName, Integer Season) {
        String[] episodes;
        File folder = new File(dir + "\\" + ShowName + "\\Season" + ' ' + Season);
        if (new FileManager().checkFolderExists(String.valueOf(folder)) && new File(String.valueOf(folder)).list().length > 0) {
            episodes = folder.list((dir1, name) -> (name.toLowerCase().endsWith(".mkv") || name.toLowerCase().endsWith(".avi") || name.toLowerCase().endsWith(".mp4")));
            return episodes;
        }
        return null;
    }
}