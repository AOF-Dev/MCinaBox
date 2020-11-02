package com.aof.mcinabox.launcher.download.authlib;

public class AuthlibManifestResponse {
    String lastest_build_number;
    Artifact[] artifacts;

    class Artifact{
        public String build_number;
        public String version;
    }
}
