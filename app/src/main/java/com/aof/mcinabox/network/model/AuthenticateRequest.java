package com.aof.mcinabox.network.model;

public class AuthenticateRequest {
    private final Agent agent;
    private final String username;
    private final String password;
    private final String clientToken;
    private final boolean requestUser;

    public AuthenticateRequest(String username, String password) {
        this(username, password, null, false);
    }

    public AuthenticateRequest(String username, String password, boolean requestUser) {
        this(username, password, null, requestUser);
    }

    public AuthenticateRequest(String username, String password, String clientToken) {
        this(username, password, clientToken, false);
    }

    public AuthenticateRequest(String username, String password, String clientToken, boolean requestUser) {
        this.agent = new Agent("Minecraft", 1);
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
        this.requestUser = requestUser;
    }

    private static class Agent {
        private final String name;
        private final int version;

        public Agent(String name, int version) {
            this.name = name;
            this.version = version;
        }
    }
}
