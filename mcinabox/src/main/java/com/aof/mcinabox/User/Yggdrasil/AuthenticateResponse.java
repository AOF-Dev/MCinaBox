package com.aof.mcinabox.User.Yggdrasil;

public class AuthenticateResponse {
    private String accessToken;  //随机访问令牌
    private String clientToken;  //如果发送了，就与发送的相同，否则为重新下发的
    private AvailableProfiles[] availableProfiles;  //仅在发送了agent字段时出现
    private SelectedProfile selectedProfile;  //仅在发送了agent字段时出现
    private User user;  //仅在请求负载中的requestUser为true出现

    private class AvailableProfiles{
        String agent;  //值为"Minecraft"
        String id;  //档案标识符
        String name;  //玩家名称
        String userId;  //用户Id
        long createdAt; //自1970年1月1日起的毫秒数
        boolean legacyProfile;
        boolean suspended;
        boolean paid;  //是否购买了游戏
        boolean migrated;
        boolean legacy;

        public String getAgent() { return agent; }
        public String getId() { return id; }
        public String getName() { return name; }
        public String getUserId() { return userId; }
        public long getCreatedAt() { return createdAt; }
        public boolean isLegacyProfile() { return legacyProfile; }
        public boolean isSuspended() { return suspended; }
        public boolean isPaid() { return paid; }
        public boolean isMigrated() { return migrated; }
        public boolean isLegacy() { return legacy; }
    }
    private class SelectedProfile{
        String id;  //不含分隔符的uuid
        String name;  //玩家名称
        String userId;
        long createdAt;
        boolean legacyProfile;
        boolean suspended;
        boolean paid;  //是否购买了游戏
        boolean migrated;
        boolean legacy;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getUserId() { return userId; }
        public long getCreatedAt() { return createdAt; }
        public boolean isLegacyProfile() { return legacyProfile; }
        public boolean isSuspended() { return suspended; }
        public boolean isPaid() { return paid; }
        public boolean isMigrated() { return migrated; }
        public boolean isLegacy() { return legacy; }
    }
    private class User{
        String id;  //不含分隔符的uuid
        String email;
        String username;
        String registerIp;  //最后一位隐藏的ip地址
        String migratedFrom;
        long migrateAt;
        long registeredAt;
        long passwordChangedAt;
        long dateOfBirth;
        boolean suspended;
        boolean blocked;
        boolean secured;
        boolean migrated;
        boolean emailVerfied;
        boolean legacyUse;
        boolean verifiedByParent;
        Properties[] properties;

        private class Properties{
            String name;
            String value;

            public String getName() { return name; }
            public String getValue() { return value; }
        }

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getRegisterIp() { return registerIp; }
        public String getMigratedFrom() { return migratedFrom; }
        public long getMigrateAt() { return migrateAt; }
        public long getRegisteredAt() { return registeredAt; }
        public long getPasswordChangedAt() { return passwordChangedAt; }
        public long getDateOfBirth() { return dateOfBirth; }
        public boolean isSuspended() { return suspended; }
        public boolean isBlocked() { return blocked; }
        public boolean isSecured() { return secured; }
        public boolean isMigrated() { return migrated; }
        public boolean isEmailVerfied() { return emailVerfied; }
        public boolean isLegacyUse() { return legacyUse; }
        public boolean isVerifiedByParent() { return verifiedByParent; }
        public Properties[] getProperties() { return properties; }
    }

    public String getAccessToken() { return accessToken; }
    public String getClientToken() { return clientToken; }
    public AvailableProfiles[] getAvailableProfiles() { return availableProfiles; }
    public SelectedProfile getSelectedProfile() { return selectedProfile; }
    public User getUser() { return user; }
}
