package com.aof.mcinabox.network.model;

public class RefreshResponse {
    private String accessToken;
    private String clientToken;
    private Profile selectedProfile;

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }
}
