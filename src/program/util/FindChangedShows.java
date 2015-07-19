package program.util;

import program.information.ChangeReporter;
import program.information.show.Show;

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

    public void findShowFileDifferences(Map<String, Show> newShowsFile) {
        log.info("findShowFileDifferences running...");
        final boolean[] hasChanged = {false};
        ArrayList<String> showsFound = new ArrayList<>();
        showsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (newShowsFile.containsKey(aShow)) {
                showsFound.add(aShow);
            } else {
                log.info(aShow + " Removed");
                ChangeReporter.addChange(aShow + Strings.WasRemoved);
                hasChanged[0] = true;
            }
        });
        showsFound.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFound = new ArrayList<>();
            showsFile.get(aShowFound).getSeasons().forEach((aSeason, aIntegerHashMap) -> {
                if (newShowsFile.get(aShowFound).getSeasons().containsKey(aSeason)) {
                    seasonsFound.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Removed");
                    ChangeReporter.addChange(aShowFound + Strings.DashSeason + aSeason + Strings.WasRemoved);
                    hasChanged[0] = true;
                }
            });
            seasonsFound.forEach(aSeasonFound -> showsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().forEach((aEpisode, episodeMap) -> {
                if (!newShowsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Removed");
                    ChangeReporter.addChange(aShowFound + Strings.DashSeason + aSeasonFound + Strings.DashEpisode + aEpisode + Strings.WasRemoved);
                    hasChanged[0] = true;
                }
            }));
        });

        ArrayList<String> showsFoundOld = new ArrayList<>();
        newShowsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (showsFile.containsKey(aShow)) {
                showsFoundOld.add(aShow);
            } else {
                log.info(aShow + " Added");
                ChangeReporter.addChange(aShow + Strings.WasAdded);
                hasChanged[0] = true;
            }
        });
        showsFoundOld.forEach(aShowFound -> {
            ArrayList<Integer> seasonsFoundOld = new ArrayList<>();
            newShowsFile.get(aShowFound).getSeasons().forEach((aSeason, aIntegerHashMap) -> {
                if (showsFile.get(aShowFound).getSeasons().containsKey(aSeason)) {
                    seasonsFoundOld.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Added");
                    ChangeReporter.addChange(aShowFound + Strings.DashSeason + aSeason + Strings.WasAdded);
                    hasChanged[0] = true;
                }
            });
            seasonsFoundOld.forEach(aSeasonFound -> newShowsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().forEach((aEpisode, StringString) -> {
                if (!showsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Added");
                    ChangeReporter.addChange(aShowFound + Strings.DashSeason + aSeasonFound + Strings.DashEpisode + aEpisode + Strings.WasAdded);
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
