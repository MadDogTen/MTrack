package program.util;

import program.information.ChangeReporter;

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
            } else {
                log.info(aShow + " Removed");
                ChangeReporter.addChange(aShow + " was removed.");
            }
        });
        showsFound.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFound = new ArrayList<>();
            showsFile.get(aShowFound).forEach((aSeason, aIntegerHashMap) -> {
                if (oldShowsFile.get(aShowFound).containsKey(aSeason)) {
                    seasonsFound.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Removed");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeason + " was removed.");
                }
            });
            seasonsFound.forEach(aSeasonFound -> showsFile.get(aShowFound).get(aSeasonFound).forEach((aEpisode, StringString) -> {
                if (!oldShowsFile.get(aShowFound).get(aSeasonFound).containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Removed");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " was removed");
                }
            }));
        });

        ArrayList<String> showsFoundOld = new ArrayList<>();
        oldShowsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (showsFile.containsKey(aShow)) {
                showsFoundOld.add(aShow);
            } else {
                log.info(aShow + " Added");
                ChangeReporter.addChange(aShow + " was added.");
            }
        });
        showsFoundOld.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFoundOld = new ArrayList<>();
            oldShowsFile.get(aShowFound).forEach((aSeason, aIntegerHashMap) -> {
                if (showsFile.get(aShowFound).containsKey(aSeason)) {
                    seasonsFoundOld.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Added");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeason + " was added.");
                }
            });
            seasonsFoundOld.forEach(aSeasonFound -> oldShowsFile.get(aShowFound).get(aSeasonFound).forEach((aEpisode, StringString) -> {
                if (!showsFile.get(aShowFound).get(aSeasonFound).containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Added");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " was added.");
                }
            }));
        });
        log.info("Finished running findShowFileDifferences.");
    }
}
