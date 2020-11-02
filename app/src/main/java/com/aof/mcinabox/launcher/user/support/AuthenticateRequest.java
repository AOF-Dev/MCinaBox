package com.aof.mcinabox.launcher.user.support;

import java.util.UUID;

public class AuthenticateRequest {
    public String username;
    public String password;
    public AgentInfo agent;
    public UUID clientToken;

    public static class AgentInfo {
        public String name;
        public int version;
    }

    public AuthenticateRequest(String username, String password, String clientToken, String clientName, int clientVersion) {
        this.username = username;
        this.password = password;
        this.clientToken = UUID.fromString(clientToken);
        this.agent = new AgentInfo();
        agent.name = clientName;
        agent.version = clientVersion;
    }
}
