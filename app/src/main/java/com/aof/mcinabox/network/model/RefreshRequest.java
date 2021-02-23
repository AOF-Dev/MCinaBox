package com.aof.mcinabox.network.model;

public class RefreshRequest {
    private final String accessToken;
    private final String clientToken;
    private final boolean requestUser;

    public RefreshRequest(String accessToken, String clientToken) {
        this(accessToken, clientToken, false);
    }

    public RefreshRequest(String accessToken, String clientToken, boolean requestUser) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.requestUser = requestUser;
    }
}
