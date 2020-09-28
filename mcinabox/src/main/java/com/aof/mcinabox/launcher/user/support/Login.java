package com.aof.mcinabox.launcher.user.support;

import android.content.*;
import android.os.*;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.user.UserManager;
import com.aof.utils.PromptUtils;
import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

public class Login extends AsyncTask<String, Void, String>
{
    private Context mContext;
    private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
    public Login(Context context) {
        this.mContext = context;
    }

    private UUID getClientId() {
        SharedPreferences prefs = mContext.getSharedPreferences(UserManager.launcher_prefs_file, 0);
        String out = prefs.getString(UserManager.auth_clientId, null);
        boolean needsRegenUUID = prefs.getBoolean(UserManager.auth_importedCredentials, false);
        UUID retval;
        if (out == null || needsRegenUUID) {
            retval = UUID.randomUUID();
            prefs.edit().putString(UserManager.auth_clientId, retval.toString()).
                    putBoolean(UserManager.auth_importedCredentials, false).
                    apply();
        } else {
            retval = UUID.fromString(out);
        }
        return retval;
    }

    @Override
    public void onPreExecute() {
        SharedPreferences prefs = mContext.getSharedPreferences(UserManager.launcher_prefs_file, 0);
    }

    @Override
    public String doInBackground(String... args) {
        try {
            AuthenticateResponse response = authenticator.authenticate(args[0], args[1], getClientId());
            if (response == null) return "Response is null?";
           if (response.selectedProfile == null) return mContext.getResources().getString(R.string.tips_login_is_demo_account);
            SharedPreferences prefs = mContext.getSharedPreferences(UserManager.launcher_prefs_file, 0);
            prefs.edit().
                    putString(UserManager.auth_accessToken, response.accessToken).
                    putString(UserManager.auth_profile_name, response.selectedProfile.name).
                    putString(UserManager.auth_profile_id, response.selectedProfile.id).
                    apply();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //写入正版用户信息
        if(s == null){
            UserManager.addAccount(MainActivity.Setting, UserManager.getOnlineAccount(mContext));
        }else{
            PromptUtils.createPrompt(mContext,s);
        }
    }
}

class YggdrasilAuthenticator {

    public static final String API_URL = "https://authserver.mojang.com";

    private String clientName = "Minecraft";
    private int clientVersion = 1;
    private Gson gson = new Gson();

    private <T> T makeRequest(String endpoint, Object inputObject, Class<T> responseClass) throws IOException {
        InputStream is = null;
        HttpURLConnection conn;
        byte[] buf = new byte[0x4000];
        int statusCode = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String requestJson = gson.toJson(inputObject);
        URL url = null;

        try {
            url = new URL(API_URL + "/" + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", clientName);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();
            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                os.write(requestJson.getBytes(Charset.forName("UTF-8")));
            } finally {
                if (os != null) os.close();
            }
            statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            for (;;) {
                int amt = is.read(buf);
                if (amt < 0)
                    break;
                bos.write(buf, 0, amt);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }

        String outString = new String(bos.toByteArray(), Charset.forName("UTF-8"));

        if (statusCode != 200) {
            throw new RuntimeException("Status: " + statusCode + ":" + outString);
        } else {
            T outResult = gson.fromJson(outString, responseClass);
            return outResult;
        }
    }

    public AuthenticateResponse authenticate(String username, String password, UUID clientId) throws IOException {
        AuthenticateRequest request = new AuthenticateRequest(username, password, clientId, clientName, clientVersion);
        return makeRequest("authenticate", request, AuthenticateResponse.class);
    }

    public RefreshResponse refresh(String authToken, UUID clientId/*, Profile activeProfile*/) throws IOException {
        RefreshRequest request = new RefreshRequest(authToken, clientId/*, activeProfile*/);
        return makeRequest("refresh", request, RefreshResponse.class);
    }
}

class RefreshResponse {
    public String accessToken;
    public UUID clientToken;
    public Profile selectedProfile;
}

class RefreshRequest {
    public String accessToken;
    public UUID clientToken;
    public RefreshRequest(String accessToken, UUID clientToken) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }
}

class AuthenticateRequest {
    public String username;
    public String password;
    public AgentInfo agent;
    public UUID clientToken;
    public static class AgentInfo {
        public String name;
        public int version;
    }

    public AuthenticateRequest(String username, String password, UUID clientToken, String clientName, int clientVersion) {
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
        this.agent = new AgentInfo();
        agent.name = clientName;
        agent.version = clientVersion;
    }
}

class AuthenticateResponse {
    public String accessToken;
    public UUID clientToken;
    public Profile[] availableProfiles;
    public Profile selectedProfile;
}

class Profile {
    public String id;
    public String name;
    public boolean legacy;
}
