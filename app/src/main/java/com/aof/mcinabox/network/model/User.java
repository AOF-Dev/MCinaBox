package com.aof.mcinabox.network.model;

public class User {
    private String id;
    private String email;
    private String username;
    private String registerIp;
    private String migratedFrom;
    private long registeredAt;
    private long passwordChangedAt;
    private long dateOfBirth;
    private boolean suspended;
    private boolean blocked;
    private boolean secured;
    private boolean migrated;
    private boolean emailVerified;
    private boolean legacyUser;
    private boolean verifiedByParent;
    private Property[] properties;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public String getMigratedFrom() {
        return migratedFrom;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    public long getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isSecured() {
        return secured;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isLegacyUser() {
        return legacyUser;
    }

    public boolean isVerifiedByParent() {
        return verifiedByParent;
    }

    public Property[] getProperties() {
        return properties;
    }

    private static class Property {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
