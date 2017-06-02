package com.maddogten.mtrack.util;

import java.io.File;

public class VideoPlayer {
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
        VLC(1000, "VLC", "vlc"), MEDIA_PLAYER_CLASSIC(1001, "MPC", "mpc-hc"), BS_PLAYER(1002, "BSPlayer", "bsplayer"), OTHER(0, "Other", "");

        private final String name;
        private final String partFileName;
        private final int ID;

        VideoPlayerEnum(int ID, String name, String partFileName) {
            this.ID = ID;
            this.name = name;
            this.partFileName = partFileName;
        }

        public static VideoPlayerEnum getVideoPlayerFromID(int ID) {
            switch (ID) {
                case 1000:
                    return VLC;
                case 1001:
                    return MEDIA_PLAYER_CLASSIC;
                case 1002:
                    return BS_PLAYER;
                case 0:
                default:
                    return OTHER;
            }
        }

        public String toString() {
            return name;
        }

        public String getPartFileName() {
            return partFileName;
        }

        public int getID() {
            return ID;
        }
    }
}
