package com.maddogten.mtrack.util;

import java.io.File;
import java.io.Serializable;

public class VideoPlayer implements Serializable {
    private static final long serialVersionUID = 6280100968782656038L;
    private VideoPlayerEnum videoPlayerEnum = VideoPlayerEnum.OTHER;
    private File videoPlayerLocation;

    public VideoPlayerEnum getVideoPlayerEnum() {
        return videoPlayerEnum;
    }

    public void setVideoPlayerEnum(VideoPlayerEnum videoPlayerEnum) {
        this.videoPlayerEnum = videoPlayerEnum;
    }

    public File getVideoPlayerLocation() {
        return videoPlayerLocation;
    }

    public void setVideoPlayerLocation(File videoPlayerLocation) {
        this.videoPlayerLocation = videoPlayerLocation;
    }

    public enum VideoPlayerEnum {
        VLC("VLC", "vlc"), MEDIA_PLAYER_CLASSIC("MPC", "mpc-hc"), BS_PLAYER("BSPlayer", "bsplayer"), OTHER("Other", "");

        private final String name;
        private final String partFileName;

        VideoPlayerEnum(String name, String partFileName) {
            this.name = name;
            this.partFileName = partFileName;
        }

        public String toString() {
            return name;
        }

        public String getPartFileName() {
            return partFileName;
        }
    }
}
