package com.maddogten.mtrack.util;

import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.information.show.Show;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/*
      FindChangedShows compares the old shows file to the new one,
      and reports any found changes to the ChangeReporter.
 */

public class FindChangedShows {
    private final Logger log = Logger.getLogger(FindChangedShows.class.getName());

    private final Map<String, Show> oldShowsFile;
    private final UserInfoController userInfoController;

    private final Map<String, Integer> showRemaining;

    // This class is initialized with a unchanged showsFile.
    public FindChangedShows(final Map<String, Show> oldShowsFile, final UserInfoController userInfoController) {
        this.oldShowsFile = oldShowsFile;
        this.userInfoController = userInfoController;

        this.showRemaining = new HashMap<>();
        if (userInfoController != null)
            userInfoController.getAllNonIgnoredShows().forEach((aShow) -> this.showRemaining.put(aShow, userInfoController.getRemainingNumberOfEpisodes(aShow)));
    }

    // This compares the showFile that is unchanged with the new updated showsFile and reports any changes found to the ChangeReporter.
    public void findShowFileDifferences(final Map<String, Show> newShowsFile) {
        log.info("findShowFileDifferences running...");
        boolean[] hasChanged = new boolean[]{false};
        oldShowsFile.forEach((showName, aShow) -> {
            if (newShowsFile.containsKey(showName)) {
                boolean recordShowInformation = this.userInfoController.isShowActive(showName) || Variables.recordChangesForNonActiveShows;
                int currentSeason = this.userInfoController.getCurrentSeason(showName);
                aShow.getSeasons().forEach((seasonInt, aSeason) -> {
                    boolean recordSeason = seasonInt >= currentSeason || Variables.recordChangedSeasonsLowerThanCurrent;
                    if ((newShowsFile.get(showName).getSeasons().containsKey(seasonInt))) {
                        aSeason.getEpisodes().forEach((episodeInt, aEpisode) -> {
                            if (!(newShowsFile.get(showName).getSeason(seasonInt).getEpisodes().containsKey(episodeInt))) {
                                this.log.info(showName + " - Season " + showName + " - Episode " + episodeInt + " Removed");
                                if (recordShowInformation && recordSeason) {
                                    ClassHandler.controller().addChangedShow(showName, showRemaining.get(showName));
                                    ChangeReporter.addChange("- " + showName + Strings.DashSeason.getValue() + seasonInt + Strings.DashEpisode.getValue() + episodeInt);
                                }
                                hasChanged[0] = true;
                            }
                        });
                    } else {
                        log.info(showName + " - Season " + seasonInt + " Removed");
                        if (recordShowInformation && recordSeason) {
                            ClassHandler.controller().addChangedShow(showName, showRemaining.get(showName));
                            ChangeReporter.addChange("- " + showName + Strings.DashSeason.getValue() + seasonInt);
                        }
                        hasChanged[0] = true;
                    }
                });
            } else {
                this.log.info(showName + " Removed");
                ChangeReporter.addChange("- " + showName);
                hasChanged[0] = true;
            }
        });

        newShowsFile.forEach((showName, aShow) -> {
            if (oldShowsFile.containsKey(showName)) {
                boolean recordShowInformation = userInfoController.isShowActive(showName) || Variables.recordChangesForNonActiveShows;
                int currentSeason = userInfoController.getCurrentSeason(showName);
                aShow.getSeasons().forEach((seasonInt, aSeason) -> {
                    boolean recordSeason = seasonInt >= currentSeason || Variables.recordChangedSeasonsLowerThanCurrent;
                    if (oldShowsFile.get(showName).getSeasons().containsKey(seasonInt)) {
                        aSeason.getEpisodes().forEach((episodeInt, aEpisode) -> {
                            if (!oldShowsFile.get(showName).getSeason(seasonInt).getEpisodes().containsKey(episodeInt)) {
                                this.log.info(showName + " - Season " + seasonInt + " - Episode " + episodeInt + " Added");
                                if (recordShowInformation && recordSeason) {
                                    ClassHandler.controller().addChangedShow(showName, showRemaining.get(showName));
                                    ChangeReporter.addChange("+ " + showName + Strings.DashSeason.getValue() + seasonInt + Strings.DashEpisode.getValue() + episodeInt);
                                }
                                hasChanged[0] = true;
                            } else if (this.oldShowsFile.get(showName).getSeason(seasonInt).getEpisode(episodeInt).getEpisodeFilename().hashCode() != aEpisode.getEpisodeFilename().hashCode()) {
                                this.log.info(showName + " - Season " + seasonInt + " - Episode " + episodeInt + " Changed");
                                this.log.info('\"' + this.oldShowsFile.get(showName).getSeason(seasonInt).getEpisode(episodeInt).getEpisodeFilename() + "\" -> \"" + aEpisode.getEpisodeFilename() + "\".");
                                if (recordShowInformation && recordSeason) {
                                    ClassHandler.controller().addChangedShow(showName, showRemaining.get(showName));
                                    ChangeReporter.addChange("~ " + showName + Strings.DashSeason.getValue() + seasonInt + Strings.DashEpisode.getValue() + episodeInt);
                                }
                                hasChanged[0] = true;
                            }
                        });
                    } else {
                        this.log.info(showName + " - Season " + seasonInt + " Added");
                        if (recordShowInformation && recordSeason) {
                            ClassHandler.controller().addChangedShow(showName, showRemaining.get(showName));
                            ChangeReporter.addChange("+ " + showName + Strings.DashSeason.getValue() + seasonInt);
                        }
                        hasChanged[0] = true;
                    }
                });
            } else {
                this.log.info(showName + " Added");
                ClassHandler.controller().addChangedShow(showName, -2);
                ChangeReporter.addChange("+ " + showName);
                hasChanged[0] = true;
            }
        });
        if (hasChanged[0]) {
            this.log.info("Some files have been changed.");
        } else {
            this.log.info("No files have changed.");
        }
        this.log.info("Finished running findShowFileDifferences.");
    }
}
