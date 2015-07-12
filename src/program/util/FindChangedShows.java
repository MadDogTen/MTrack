package program.util;

import program.information.ChangeReporter;
import program.information.Show;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class FindChangedShows {
    private final Logger log = Logger.getLogger(FindChangedShows.class.getName());

    private final Map<String, Show> showsFile;

    public FindChangedShows(Map<String, Show> showsFile) {
        this.showsFile = showsFile;
        log.info("ShowsFile has been set.");
    }

    public void findShowFileDifferences(Map<String, Show> oldShowsFile) {
        log.info("findShowFileDifferences running...");
        final boolean[] hasChanged = {false};
        ArrayList<String> showsFound = new ArrayList<>();
        showsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (oldShowsFile.containsKey(aShow)) {
                showsFound.add(aShow);
            } else {
                log.info(aShow + " Removed");
                ChangeReporter.addChange(aShow + " was removed.");
                hasChanged[0] = true;
            }
        });
        showsFound.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFound = new ArrayList<>();
            showsFile.get(aShowFound).getSeasons().forEach((aSeason, aIntegerHashMap) -> {
                if (oldShowsFile.get(aShowFound).getSeasons().containsKey(aSeason)) {
                    seasonsFound.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Removed");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeason + " was removed.");
                    hasChanged[0] = true;
                }
            });
            seasonsFound.forEach(aSeasonFound -> showsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().forEach((aEpisode, episodeMap) -> {
                if (!oldShowsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Removed");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " was removed");
                    hasChanged[0] = true;
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
                hasChanged[0] = true;
            }
        });
        showsFoundOld.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFoundOld = new ArrayList<>();
            oldShowsFile.get(aShowFound).getSeasons().forEach((aSeason, aIntegerHashMap) -> {
                if (showsFile.get(aShowFound).getSeasons().containsKey(aSeason)) {
                    seasonsFoundOld.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Added");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeason + " was added.");
                    hasChanged[0] = true;
                }
            });
            seasonsFoundOld.forEach(aSeasonFound -> oldShowsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().forEach((aEpisode, StringString) -> {
                if (!showsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Added");
                    ChangeReporter.addChange(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " was added.");
                    hasChanged[0] = true;
                }
            }));
        });
        if (hasChanged[0]) {
            log.info("Some files have been changed.");
        } else {
            log.info("No files have changed.");
        }
        log.info("Finished running findShowFileDifferences.");
    }
}
