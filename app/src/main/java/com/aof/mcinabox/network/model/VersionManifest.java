package com.aof.mcinabox.network.model;

import java.util.Date;

public class VersionManifest {
    public static final String TYPE_SNAPSHOT = "snapshot";
    public static final String TYPE_RELEASE = "release";
    public static final String TYPE_OLD_BETA = "old_beta";
    public static final String TYPE_OLD_ALPHA = "old_alpha";

    public Latest latest;
    public Version[] versions;

    public Latest getLatest() {
        return latest;
    }

    public Version[] getVersions() {
        return versions;
    }

    private static class Latest {
        private String release;
        private String snapshot;

        public String getRelease() {
            return release;
        }

        public String getSnapshot() {
            return snapshot;
        }
    }

    public static class Version {
        private String id;
        private String type;
        private String url;
        private Date time;
        private Date releaseTime;

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public Date getTime() {
            return time;
        }

        public Date getReleaseTime() {
            return releaseTime;
        }
    }
}
