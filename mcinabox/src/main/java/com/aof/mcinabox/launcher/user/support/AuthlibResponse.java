package com.aof.mcinabox.launcher.user.support;

import com.google.gson.annotations.SerializedName;

public class AuthlibResponse {
    public String[] skinDomains;
    public String signaturePublickey;
    public ServerMeta meta;
    
    public class ServerMeta {
        public String implementationName;
        public String implementationVersion;
        public String serverName;
        @SerializedName("feature.non_email_login")
        public boolean isNonEmailLogin;
    }
}
