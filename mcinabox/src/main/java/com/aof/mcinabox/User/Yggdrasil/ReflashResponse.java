package com.aof.mcinabox.User.Yggdrasil;

public class ReflashResponse {
    private String accessToken;
    private String clientToken;
    private SelectedProfile selectedProfile;
    private  User user;

    private class SelectedProfile{
        String id;
        String name;
        public String getId() { return id; }
        public String getName() { return name; }
    }
    private class User{
        String id;
        Properties[] properties;

        private class Properties{
            String name;
            String value;
            public String getName() { return name; }
            public String getValue() { return value; }
        }
        public String getId() { return id; }
        public Properties[] getProperties() { return properties; }
    }
    public String getAccessToken() { return accessToken; }
    public String getClientToken() { return clientToken; }
    public SelectedProfile getSelectedProfile() { return selectedProfile; }
    public User getUser() { return user; }
}
