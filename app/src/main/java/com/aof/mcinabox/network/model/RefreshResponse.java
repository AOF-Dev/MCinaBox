package com.aof.mcinabox.network.model;

public class RefreshResponse {
    private String accessToken;
    private String clientToken;
    private GameProfile selectedGameProfile;

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public GameProfile getSelectedProfile() {
        return selectedGameProfile;
    }
}
