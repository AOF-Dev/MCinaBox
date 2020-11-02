package com.aof.mcinabox.launcher.download.authlib;

public class AuthlibVersionResponse {
    String build_number;
    String version;
    String download_url;
    Checksum checksums;

    class Checksum{
        public String sha256;
    }
}
