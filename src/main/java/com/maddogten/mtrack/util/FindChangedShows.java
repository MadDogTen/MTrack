package com.maddogten.mtrack.util;

import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.information.show.Show;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class FindChangedShows {
    private final Logger log = Logger.getLogger(FindChangedShows.class.getName());

    private final Map<String, Show> showsFile;
    private final UserInfoController userInfoController;

    // This class is initialized with a unchanged showsFile.
    public FindChangedShows(Map<String, Show> showsFile, UserInfoController userInfoController) {
        this.showsFile = showsFile;
        this.userInfoController = userInfoController;
        log.info("ShowsFile has been set.");
    }

    // This compares the showFile that is unchanged with the new updated showsFile and reports any changes found to the ChangeReporter.
    public void findShowFileDifferences(Map<String, Show> newShowsFile) {
        log.info("findShowFileDifferences running...");
        final boolean[] hasChanged = {false};
        ArrayList<String> showsFound = new ArrayList<>();
        showsFile.forEach((aShow, aHashMapIntegerHashMap) -> {
            if (newShowsFile.containsKey(aShow)) {
                showsFound.add(aShow);
            } else {
                log.info(aShow + " Removed");
                ChangeReporter.addChange("- " + aShow);
                hasChanged[0] = true;
            }
        });
        showsFound.forEach(aShowFound -> {
            boolean recordShowInformation = userInfoController.isShowActive(aShowFound) || Variables.recordChangesForNonActiveShows;
            int currentSeason = userInfoController.getCurrentSeason(aShowFound);
            ArrayList<Integer> seasonsFound = new ArrayList<>();
            showsFile.get(aShowFound).getSeasons().forEach((aSeason, aIntegerHashMap) -> {
                boolean recordSeason = aSeason >= currentSeason || Variables.recordChangedSeasonsLowerThanCurrent;
                if (newShowsFile.get(aShowFound).getSeasons().containsKey(aSeason)) {
                    if (recordShowInformation && recordSeason) seasonsFound.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Removed");
                    if (recordShowInformation && recordSeason)
                        ChangeReporter.addChange("- " + aShowFound + Strings.DashSeason + aSeason);
                    hasChanged[0] = true;
                }
            });
            seasonsFound.forEach(aSeasonFound -> showsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().forEach((aEpisode, episodeMap) -> {
                if (!newShowsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Removed");
                    if (recordShowInformation && aSeasonFound >= currentSeason || Variables.recordChangedSeasonsLowerThanCurrent)
                        ChangeReporter.addChange("- " + aShowFound + Strings.DashSeason + aSeasonFound + Strings.DashEpisode + aEpisode);
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
                ChangeReporter.addChange("+ " + aShow);
                hasChanged[0] = true;
            }
        });
        showsFoundOld.forEach(aShowFound -> {
            boolean recordShowInformation = userInfoController.isShowActive(aShowFound) || Variables.recordChangesForNonActiveShows;
            int currentSeason = userInfoController.getCurrentSeason(aShowFound);
            ArrayList<Integer> seasonsFoundOld = new ArrayList<>();
            newShowsFile.get(aShowFound).getSeasons().forEach((aSeason, aIntegerHashMap) -> {
                boolean recordSeason = aSeason >= currentSeason || Variables.recordChangedSeasonsLowerThanCurrent;
                if (showsFile.get(aShowFound).getSeasons().containsKey(aSeason)) {
                    if (recordShowInformation && recordSeason) seasonsFoundOld.add(aSeason);
                } else {
                    log.info(aShowFound + " - Season " + aSeason + " Added");
                    if (recordShowInformation && recordSeason)
                        ChangeReporter.addChange("+ " + aShowFound + Strings.DashSeason + aSeason);
                    hasChanged[0] = true;
                }
            });
            seasonsFoundOld.forEach(aSeasonFound -> newShowsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().forEach((aEpisode, StringString) -> {
                if (!showsFile.get(aShowFound).getSeason(aSeasonFound).getEpisodes().containsKey(aEpisode)) {
                    log.info(aShowFound + " - Season " + aSeasonFound + " - Episode " + aEpisode + " Added");
                    if (recordShowInformation && aSeasonFound >= currentSeason || Variables.recordChangedSeasonsLowerThanCurrent)
                        ChangeReporter.addChange("+ " + aShowFound + Strings.DashSeason + aSeasonFound + Strings.DashEpisode + aEpisode);
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
