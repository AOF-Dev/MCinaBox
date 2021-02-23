package com.aof.mcinabox.launcher.user.support;

import java.util.UUID;

public class AuthenticateResponse {
    public String accessToken;
    public UUID clientToken;
    public Profile[] availableProfiles;
    public Profile selectedProfile;
}
