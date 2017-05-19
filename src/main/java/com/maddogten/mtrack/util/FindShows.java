package com.maddogten.mtrack.util;

import com.maddogten.mtrack.io.FileManager;
import javafx.concurrent.Task;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    public final Set<Show> findShows(final File dir) {
        Set<Show> result = new HashSet<>();
        Set<String> uncheckedShows = new HashSet<>(Arrays.asList(dir.list((dir1, name) -> new File(dir1 + Strings.FileSeparator + name).isDirectory())));
        Set<Thread> threads = new HashSet<>();
        uncheckedShows.forEach(showName -> threads.add(new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Show show = new Show(dir, showName);
                if (show.hasSeasons()) result.add(show);
                return null;
            }
        })));
        threads.forEach(Thread::start);
        while (!threads.isEmpty()) {
            threads.removeIf(next -> !next.isAlive());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        return result;
    }

    private Set<Integer> findSeasons(final File dir, final String show) {
        Set<String> showFolder = new HashSet<>();
        File folder = new File(dir + Strings.FileSeparator + show);
        if (folder.isDirectory() && folder.list() != null) {
            showFolder.addAll(Arrays.asList(new File(dir + Strings.FileSeparator + show).list((dir1, name) -> new File(dir1 + Strings.FileSeparator + name).isDirectory())));
            Set<Integer> seasonNumber = new HashSet<>(showFolder.size());
            Pattern pattern = Pattern.compile(Strings.seasonRegex + "\\s" + Strings.seasonNumberRegex);
            Pattern pattern1 = Pattern.compile("s" + Strings.seasonNumberRegex);
            showFolder.forEach(aShowFolder -> {
                String aShowFolderLowercase = aShowFolder.toLowerCase();
                Matcher matcher = pattern.matcher(aShowFolderLowercase);
                if (matcher.find()) seasonNumber.add(Integer.parseInt(matcher.group().toLowerCase().split(" ")[1]));
                else {
                    matcher = pattern1.matcher(aShowFolderLowercase);
                    if (matcher.find())
                        seasonNumber.add(Integer.parseInt(matcher.group().toLowerCase().replace("s", "")));
                }
            });
            return seasonNumber;
        } else log.fine("Folder for " + show + " was found to be null.");
        return new HashSet<>();
    }

    private Set<String> findEpisodes(final File dir, final String showName, final int season) {
        String seasonFolder = GenericMethods.getSeasonFolderName(dir, showName, season);
        if (!seasonFolder.isEmpty()) {
            File folder = new File(dir + Strings.FileSeparator + showName + Strings.FileSeparator + seasonFolder);
            if (folder.exists() && new FileManager().checkFolderExistsAndReadable(folder) && new File(String.valueOf(folder)).list().length > 0)
                return new HashSet<>(Arrays.asList(folder.list((dir1, name) -> {
                    if (new File(dir1 + Strings.FileSeparator + name).isFile()) {
                        String lowercaseName = name.toLowerCase();
                        for (String extension : Variables.showExtensions)
                            if (lowercaseName.endsWith(extension))
                                return true;
                    }
                    return false;
                })));
        }
        return new HashSet<>(0);
    }

    public class Show {
        private final String show;
        private final Set<Season> seasons;

        public Show(File directory, String show) {
            this.show = show;
            this.seasons = new HashSet<>();

            findSeasons(directory, show).forEach(seasonInt -> {
                Season season = new Season(directory, seasonInt);
                if (season.hasEpisodes()) seasons.add(season);
            });
        }

        public String getShow() {
            return show;
        }

        public Set<Season> getSeasons() {
            return seasons;
        }

        boolean hasSeasons() {
            return !seasons.isEmpty();
        }

        public class Season {
            private final int season;
            private final Set<Episode> episodes;

            public Season(File directory, int season) {
                this.season = season;
                this.episodes = new HashSet<>();
                findEpisodes(directory, show, season).forEach(episodeFilename -> {
                    Episode episode = new Episode(episodeFilename);
                    if (episode.episode != -2) episodes.add(episode);
                });
            }

            public int getSeason() {
                return season;
            }

            public Set<Episode> getEpisodes() {
                return episodes;
            }

            boolean hasEpisodes() {
                return !episodes.isEmpty();
            }

            public class Episode {
                private final int episode;
                private final int episode2;
                private final boolean doubleEpisode;
                private final String episodeFilename;
                private final int episodeHash;

                public Episode(String episodeFilename) {
                    this.episodeFilename = episodeFilename;
                    int[] episodeInfo = ClassHandler.showInfoController().getEpisodeInfo(this.episodeFilename);
                    this.episode = episodeInfo[0];
                    this.episode2 = episodeInfo[1];
                    this.doubleEpisode = (this.episode2 != -2);
                    this.episodeHash = this.episodeFilename.hashCode();
                }

                public int getEpisode() {
                    return this.episode;
                }

                public int getEpisode2() {
                    return this.episode2;
                }

                public boolean isDoubleEpisode() {
                    return this.doubleEpisode;
                }

                public String getEpisodeFilename() {
                    return this.episodeFilename;
                }

                public int getEpisodeHash() {
                    return episodeHash;
                }
            }
        }
    }
}
