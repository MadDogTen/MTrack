package program.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class FindChangedShows {
    private final Logger log = Logger.getLogger(FindChangedShows.class.getName());

    private HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile;

    public void findChangedShows(HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        this.showsFile = showsFile;
        log.info("ShowsFile has been set.");
    }

    public void findShowFileDifferences(HashMap<String, HashMap<Integer, HashMap<String, String>>> oldShowsFile) {
        log.info("findShowFileDifferences running...");
        ArrayList<String> showsFound = new ArrayList<>();
        showsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (oldShowsFile.containsKey(aShow)) {
                showsFound.add(aShow);
            } else log.info(aShow + " Removed");
        });
        showsFound.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFound = new ArrayList<>();
            showsFile.get(aShowFound).forEach((aSeason, aIntegerHashMap) -> {
                if (oldShowsFile.get(aShowFound).containsKey(aSeason)) {
                    seasonsFound.add(aSeason);
                } else log.info(aShowFound + " - " + aSeason + " Removed");
            });
            seasonsFound.forEach(aSeasonFound -> {
                showsFile.get(aShowFound).get(aSeasonFound).forEach((aEpisode, StringString) -> {
                    if (!oldShowsFile.get(aShowFound).get(aSeasonFound).containsKey(aEpisode)) {
                        log.info(aShowFound + " - " + aSeasonFound + " - " + aEpisode + " Removed");
                    }
                });
            });
        });

        ArrayList<String> showsFoundOld = new ArrayList<>();
        oldShowsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (showsFile.containsKey(aShow)) {
                showsFoundOld.add(aShow);
            } else log.info(aShow + " Added");
        });
        showsFoundOld.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFoundOld = new ArrayList<>();
            oldShowsFile.get(aShowFound).forEach((aSeason, aIntegerHashMap) -> {
                if (showsFile.get(aShowFound).containsKey(aSeason)) {
                    seasonsFoundOld.add(aSeason);
                } else log.info(aShowFound + " - " + aSeason + " Added");
            });
            seasonsFoundOld.forEach(aSeasonFound -> {
                oldShowsFile.get(aShowFound).get(aSeasonFound).forEach((aEpisode, StringString) -> {
                    if (!showsFile.get(aShowFound).get(aSeasonFound).containsKey(aEpisode)) {
                        log.info(aShowFound + " - " + aSeasonFound + " - " + aEpisode + " Added");
                    }
                });
            });
        });
        log.info("Finished running findShowFileDifferences.");
    }
}
