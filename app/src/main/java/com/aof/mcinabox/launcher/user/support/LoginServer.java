package com.aof.mcinabox.launcher.user.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.UserManager;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginServer {
    private final Context mContext;
    private static final String OUTPUT_RESULT = "Result";
    private static final String OUTPUT_TYPE = "Type";
    private static final String TYPE_ERROR = "Error";
    private static final String TYPE_LOGIN = "Login";
    private static final String TYPE_VALIDATE = "Validate";
    private static final String TYPE_VERIFY_SERVER = "VerifyServer";
    private static final String TAG = "LoginServer";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String MOJANG_URL = "https://authserver.mojang.com";
    private Callback mCallback;
    private boolean callable = false;

    private String username;
    private String password;
    private boolean isLogining;

    private final SettingJson.Account account;

    public LoginServer setCallback(Callback call) {
        this.mCallback = call;
        callable = (call != null);
        return this;
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String type = data.getString(OUTPUT_TYPE);
            String result = data.getString(OUTPUT_RESULT);
            output(type, result);
        }
    };
    private final okhttp3.Callback loginResponse = new okhttp3.Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            isLogining = false;
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString(OUTPUT_TYPE, TYPE_ERROR);
            data.putString(OUTPUT_RESULT, gson.toJson(e));
            msg.setData(data);
            handler.sendMessage(msg);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            isLogining = false;
            Message msg = new Message();
            Bundle data = new Bundle();

            try {
                String result = Objects.requireNonNull(response.body()).string();
                if (response.code() == 200) {
                    data.putString(OUTPUT_TYPE, TYPE_LOGIN);
                    data.putString(OUTPUT_RESULT, result);
                } else {
                    ErrorResponse error = gson.fromJson(result, ErrorResponse.class);
                    data.putString(OUTPUT_TYPE, TYPE_ERROR);
                    data.putString(OUTPUT_RESULT, gson.toJson(new Exception(error.errorMessage)));
                }
            } catch (Exception e) {
                data.putString(OUTPUT_TYPE, TYPE_ERROR);
                data.putString(OUTPUT_RESULT, gson.toJson(e));
            }

            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    public LoginServer(SettingJson.Account account, Context context){
        this(account.getApiUrl(), account, context);
    }

    public LoginServer(String url, Context context){
        this(url, new SettingJson().new Account(),context);
    }

    public LoginServer(String url, SettingJson.Account account, Context context) {
        this.mContext = context;
        if(url == null || url.equals("")) account.setApiUrl(MOJANG_URL);
        else if (!url.startsWith("http")) account.setApiUrl("https://".concat(url));
        else account.setApiUrl(url);
        this.account = account;
        isLogining = false;
    }

    private void verifyServer() {
        Request request = new Request.Builder().url(account.getApiUrl()).build();
        try {
            client.newCall(request).enqueue(verifyServerResponse);
        }catch (Exception e) {
            output(TYPE_ERROR, e.getMessage());
        }
    }

    public void login(String username, String password) {
        if(callable){
            mCallback.onStart();
        }
        this.username = username;
        this.password = password;
        account.setUserUuid(UserManager.createUUID(username).toString());
        isLogining = true;
        if(account.getApiUrl().equals(MOJANG_URL))  {
            account.setType(SettingJson.USER_TYPE_ONLINE);
            login();
        }else {
            account.setType(SettingJson.USER_TYPE_EXTERNAL);
            verifyServer();
        }
    }

    public void verifyToken() {
        if(callable){
            mCallback.onStart();
        }
        httpPost("/validate", new ValidateRequest(account.getAccessToken()), verifyTokenResponse);
    }

    public void refreshToken() {
        if(callable){
            mCallback.onStart();
        }
        httpPost("/refresh", new RefreshRequest(account.getAccessToken(), UUID.fromString(account.getUserUUID())), loginResponse);
    }

    private void login() {
        httpPost("/authenticate", new AuthenticateRequest(username, password, account.getUserUUID(), "Minecraft", 1), loginResponse);
    }

    private <T> void httpPost(String api, T data, okhttp3.Callback response) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(data));
        Request request = new Request.Builder().url(account.getApiUrl() + (account.getApiUrl().equals(MOJANG_URL)?api:"/authserver".concat(api))).post(body).build();
        try {
            client.newCall(request).enqueue(response);
        }catch (Exception e) {
            output(TYPE_ERROR, e.getMessage());
        }
    }

    private final okhttp3.Callback verifyServerResponse = new okhttp3.Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString(OUTPUT_TYPE, TYPE_ERROR);
            data.putString(OUTPUT_RESULT, gson.toJson(e));
            msg.setData(data);
            handler.sendMessage(msg);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            Message msg = new Message();
            Bundle data = new Bundle();

            try {
                if(response.toString().contains("x-authlib-injector-api-location")) {
                    account.setApiUrl(response.request().url().toString());
                    verifyServer();
                    data.putString(OUTPUT_TYPE, TYPE_VERIFY_SERVER);
                    data.putString(OUTPUT_RESULT, OldMainActivity.CURRENT_ACTIVITY.get().getResources().getString(R.string.tips_redirecting));
                }else {
                    if(response.code() == 200) data.putString(OUTPUT_TYPE, TYPE_VERIFY_SERVER);
                    else data.putString(OUTPUT_TYPE, TYPE_ERROR);
                    data.putString(OUTPUT_RESULT, Objects.requireNonNull(response.body()).string());
                }
            } catch (Exception e) {
                data.putString(OUTPUT_TYPE, TYPE_ERROR);
                data.putString(OUTPUT_RESULT, gson.toJson(e));
            }

            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
    private final okhttp3.Callback verifyTokenResponse = new okhttp3.Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString(OUTPUT_TYPE, TYPE_ERROR);
            data.putString(OUTPUT_RESULT, gson.toJson(e));
            msg.setData(data);
            handler.sendMessage(msg);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            isLogining = false;
            Message msg = new Message();
            Bundle data = new Bundle();

            try {
                String result = Objects.requireNonNull(response.body()).string();
                data.putString(OUTPUT_TYPE, TYPE_VALIDATE);
                data.putString(OUTPUT_RESULT, gson.toJson(response.code()));
            } catch (Exception e) {
                data.putString(OUTPUT_TYPE, TYPE_ERROR);
                data.putString(OUTPUT_RESULT, gson.toJson(e));
            }
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    public LoginServer(String url) {
        this(url, OldMainActivity.CURRENT_ACTIVITY.get());
    }

    public LoginServer(SettingJson.Account account) {
        this(account, OldMainActivity.CURRENT_ACTIVITY.get());
    }

    private void output(String type, String result) {
        if (result != null)
            switch (type) {
                case TYPE_VERIFY_SERVER:
                    AuthlibResponse authlibResponse = gson.fromJson(result, AuthlibResponse.class);
                    account.setServerName(authlibResponse.meta.serverName);
                    account.setApiMeta(Base64.encodeToString(result.getBytes(), Base64.DEFAULT));
                    if (isLogining) login();
                    break;
            case TYPE_LOGIN:
                if(callable){
                    mCallback.onLoginSuccess(account,gson.fromJson(result, AuthenticateResponse.class));
                }
                break;
            case TYPE_ERROR:
                Exception e = gson.fromJson(result,Exception.class);
                if(callable){
                    mCallback.onFailed(e);
                }
                break;
            case TYPE_VALIDATE:
                Integer code = gson.fromJson(result,Integer.class);
                if(callable){
                    switch (code){
                        case 204:
                            mCallback.onValidateSuccess(account);
                            break;
                        case 403:
                            mCallback.onValidateFailed(account);
                            break;
                        default:
                            mCallback.onFailed(new Exception(String.format("Unknown status code : %s",code)));
                    }
                }
                break;
        }
        if(callable){
            mCallback.onFinish();
        }
    }

    public interface Callback{
        void onStart();
        void onFailed(Exception e);
        void onLoginSuccess(SettingJson.Account account, AuthenticateResponse response);
        void onValidateSuccess(SettingJson.Account account);
        void onValidateFailed(SettingJson.Account account);
        void onRefreshSuccess(SettingJson.Account account, AuthenticateResponse response);
        void onFinish();
    }
}