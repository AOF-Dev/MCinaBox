package com.aof.mcinabox.launcher.user.support;

import android.content.*;
import android.os.*;
import android.util.Log;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.user.UserManager;
import com.aof.utils.PromptUtils;
import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

public class Login extends AsyncTask<String, Void, String> {
    private Context mContext;
    private YggdrasilAuthenticator authenticator;

    public final static String REQUEST_MDOE_REFRESH = "refresh";
    public final static String REQUEST_MODE_AUTHENTICATE = "authenticate";
    public final static String REQUEST_MODE_VALIDATE = "validate";

    public final static String HTTP_RESPONSE_204 = "204 No Content";
    public final static String HTTP_RESPONSE_403 = "403 Forbidden";

    private final static String TAG = "Login";

    private String tag;

    public Login(Context context) {
        this.mContext = context;
    }

    public UUID getClientId() {
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

    /*参数代表的意义
    if args[0] -> "refresh"
    then args[1] -> String accessToken
         args[2] -> String clientToken

    if args[0] -> "authenticate"
    then args[1] -> String username
         args[2] -> String password
         args[3] -> String accountUUID

     if args[0] -> "validate"
     then args[1] -> String accessToken
     */

    @Override
    public String doInBackground(String... args) {
        tag = args[0];
        authenticator = new YggdrasilAuthenticator(tag);
        switch (args[0]) {
            case REQUEST_MDOE_REFRESH:
                try {
                    Object response = authenticator.refresh(args[1], UUID.fromString(args[2]));
                    if (response == null) return "Response is null?";
                    if(response instanceof  RefreshResponse){
                        RefreshResponse rr = (RefreshResponse) response;
                        if (rr.selectedProfile == null)
                            return mContext.getResources().getString(R.string.tips_login_is_demo_account);
                        SharedPreferences prefs = mContext.getSharedPreferences(UserManager.launcher_prefs_file, 0);
                        prefs.edit().
                                putString(UserManager.auth_accessToken, rr.accessToken).
                                putString(UserManager.auth_profile_name, rr.selectedProfile.name).
                                putString(UserManager.auth_profile_id, rr.selectedProfile.id).
                                apply();
                        return null;
                    }else if(response instanceof ErrorResponse){
                        ErrorResponse er = (ErrorResponse) response;
                        return String.format("Exception: %s\nMessage: %s", er.error, er.errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
            case REQUEST_MODE_AUTHENTICATE:
                try {
                    Object response = authenticator.authenticate(args[1], args[2], UUID.fromString(args[3]));
                    if (response == null) return "Response is null?";
                    if(response instanceof  AuthenticateResponse){
                        AuthenticateResponse ar = (AuthenticateResponse) response;
                        if (ar.selectedProfile == null)
                            return mContext.getResources().getString(R.string.tips_login_is_demo_account);
                        Log.e(TAG, "Authenticate.");
                        SharedPreferences prefs = mContext.getSharedPreferences(UserManager.launcher_prefs_file, 0);
                        prefs.edit().
                                putString(UserManager.auth_accessToken, ar.accessToken).
                                putString(UserManager.auth_profile_name, ar.selectedProfile.name).
                                putString(UserManager.auth_profile_id, ar.selectedProfile.id).
                                apply();
                        return null;
                    }else if(response instanceof ErrorResponse){
                        ErrorResponse er = (ErrorResponse) response;
                        return String.format("Exception: %s\nMessage: %s", er.error, er.errorMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
            case REQUEST_MODE_VALIDATE:
                try {
                    HttpResponse response = authenticator.validate(args[1]);
                    if (response == null) return "Response is null?";
                    switch (response.httpResponse){
                        case 204:
                            return HTTP_RESPONSE_204;
                        case 403:
                            return HTTP_RESPONSE_403;
                        default:
                            return "Unknown statCode: %d" + response.httpResponse;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        switch (tag) {
            case REQUEST_MODE_AUTHENTICATE:
                if (s == null) {
                    Log.e(TAG, "create online account");
                    UserManager.addAccount(MainActivity.Setting, UserManager.getOnlineAccount(mContext));
                } else {
                    PromptUtils.createPrompt(mContext, s);
                }
                break;
            case REQUEST_MODE_VALIDATE:
                if (s == null) {
                    UserManager.addAccount(MainActivity.Setting, UserManager.getOnlineAccount(mContext));
                } else {
                    PromptUtils.createPrompt(mContext, s);
                }
                break;
            case REQUEST_MDOE_REFRESH:
                break;
        }

    }
}

class YggdrasilAuthenticator {

    public static final String API_URL = "https://authserver.mojang.com";

    private String clientName = "Minecraft";
    private int clientVersion = 1;
    private Gson gson = new Gson();
    private String tag;

    public YggdrasilAuthenticator(String tag) {
        this.tag = tag;
    }

    private Object makeRequest(String endpoint, Object inputObject) throws IOException {
        InputStream is = null;
        HttpURLConnection conn;
        byte[] buf = new byte[0x4000];
        int statusCode = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String requestJson = gson.toJson(inputObject);
        URL url;

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

            for (; ; ) {
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
                    e.printStackTrace();
                }
            }
        }
        String outString = new String(bos.toByteArray(), Charset.forName("UTF-8"));

        switch (tag) {
            case Login.REQUEST_MDOE_REFRESH:
                if (statusCode != 200) {
                    return gson.fromJson(outString, ErrorResponse.class);
                } else {
                    return gson.fromJson(outString, RefreshResponse.class);
                }
            case Login.REQUEST_MODE_AUTHENTICATE:
                if (statusCode != 200) {
                    return gson.fromJson(outString, ErrorResponse.class);
                } else {
                    return gson.fromJson(outString, AuthenticateResponse.class);
                }
            case Login.REQUEST_MODE_VALIDATE:
                return new HttpResponse(statusCode);
        }
        return null;
    }

    public Object authenticate(String username, String password, UUID clientId) throws IOException {
        AuthenticateRequest request = new AuthenticateRequest(username, password, clientId, clientName, clientVersion);
        return makeRequest("authenticate", request);
    }

    public Object refresh(String authToken, UUID clientId/*, Profile activeProfile*/) throws IOException {
        RefreshRequest request = new RefreshRequest(authToken, clientId/*, activeProfile*/);
        return makeRequest("refresh", request);
    }

    public HttpResponse validate(String accessToken) throws IOException {
        ValidateRequest request = new ValidateRequest(accessToken);
        Object rs = makeRequest("validate", request);
        if(rs instanceof HttpResponse){
            return (HttpResponse) rs;
        }else{
            return null;
        }
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

class HttpResponse {
    public int httpResponse;

    public HttpResponse(int response) {
        this.httpResponse = response;
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

class ValidateRequest {
    public String accessToken;

    public ValidateRequest(String accessToken) {
        this.accessToken = accessToken;
    }
}

class ErrorResponse {
    public String error;
    public String errorMessage;
    public String cause;
}
