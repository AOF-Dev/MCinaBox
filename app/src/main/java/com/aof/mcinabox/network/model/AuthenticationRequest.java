package com.aof.mcinabox.network.model;

public class AuthenticationRequest {
    private final Agent agent;
    private final String username;
    private final String password;
    private final String clientToken;
    private final boolean requestUser;

    public AuthenticationRequest(String username, String password) {
        this(username, password, null, true);
    }

    public AuthenticationRequest(String username, String password, boolean requestUser) {
        this(username, password, null, requestUser);
    }

    public AuthenticationRequest(String username, String password, String clientToken) {
        this(username, password, clientToken, true);
    }

    public AuthenticationRequest(String username, String password, String clientToken, boolean requestUser) {
        this.agent = Agent.MINECRAFT;
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
        this.requestUser = requestUser;
    }

    private static class Agent {
        public static final Agent MINECRAFT = new Agent("Minecraft", 1);
        public static final Agent SCROLLS = new Agent("Scrolls", 1);

        private final String name;
        private final int version;

        private Agent(String name, int version) {
            this.name = name;
            this.version = version;
        }
    }
}
