package com.aof.mcinabox.launcher.user.support;

import java.util.UUID;

public class RefreshRequest {
    public String accessToken;
    public UUID clientToken;
    public Profile selectedProfile;

    public RefreshRequest(String accessToken, UUID clientToken, String  id, String name) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        selectedProfile = new Profile(id, name);
    }
}
