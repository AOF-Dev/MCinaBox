package com.aof.mcinabox.network;

import android.util.Log;

import com.aof.mcinabox.network.gson.DateDeserializer;
import com.aof.mcinabox.network.gson.LowerCaseEnumTypeAdapterFactory;
import com.aof.mcinabox.network.gson.ReleaseTypeAdapterFactory;
import com.aof.mcinabox.network.gson.VersionDeserializer;
import com.aof.mcinabox.network.model.AuthenticationRequest;
import com.aof.mcinabox.network.model.AuthenticationResponse;
import com.aof.mcinabox.network.model.ErrorResponse;
import com.aof.mcinabox.network.model.RefreshRequest;
import com.aof.mcinabox.network.model.RefreshResponse;
import com.aof.mcinabox.network.model.ReleaseType;
import com.aof.mcinabox.network.model.ValidateRequest;
import com.aof.mcinabox.network.model.Version;
import com.aof.mcinabox.network.model.VersionManifest;
import com.aof.mcinabox.utils.SkinUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MojangRepository {
    private static final String API_BASE_URL = "https://api.mojang.com";
    private static final String AUTH_BASE_URL = "https://authserver.mojang.com";
    private static final String LAUNCHERMETA_BASE_URL = "https://launchermeta.mojang.com";
    private static final String SESSION_BASE_URL = "https://sessionserver.mojang.com";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static MojangRepository instance = null;

    private final OkHttpClient client;
    private final Gson gson;

    private MojangRepository() {
        client = new OkHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory())
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(ReleaseType.class, new ReleaseTypeAdapterFactory())
                .registerTypeAdapter(Version.class, new VersionDeserializer())
                .create();
    }

    public void authenticate(AuthenticationRequest authenticationRequest, Callback<AuthenticationResponse> callback) {
        postJson(AUTH_BASE_URL + "/authenticate", authenticationRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader body = response.body().charStream()) {
                    if (response.code() == 200) {
                        callback.onSuccess(gson.fromJson(body, AuthenticationResponse.class));
                    } else {
                        callback.onError(gson.fromJson(body, ErrorResponse.class));
                    }
                } catch (IOException e) {
                    callback.onError(null);
                }
            }
        });
    }

    public void refresh(RefreshRequest validateRequest, Callback<RefreshResponse> callback) {
        postJson(AUTH_BASE_URL + "/refresh", validateRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader body = response.body().charStream()) {
                    if (response.code() == 200) {
                        callback.onSuccess(gson.fromJson(body, RefreshResponse.class));
                    } else {
                        callback.onError(gson.fromJson(body, ErrorResponse.class));
                    }
                } catch (IOException e) {
                    callback.onError(null);
                }
            }
        });
    }

    public void validate(ValidateRequest validateRequest, Callback<Void> callback) {
        postJson(AUTH_BASE_URL + "/validate", validateRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader body = response.body().charStream()) {
                    if (response.code() == 204) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(gson.fromJson(body, ErrorResponse.class));
                    }
                } catch (IOException e) {
                    callback.onError(null);
                }
            }
        });
    }

    public void skin(String id, Callback<String> callback) {
        get(SESSION_BASE_URL + "/session/minecraft/profile/" + id).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader body = response.body().charStream()) {
                    if (response.code() == 200) {
                        callback.onSuccess(SkinUtils.getPlayerSkin(body));
                    } else {
                        callback.onError(gson.fromJson(body, ErrorResponse.class));
                    }
                } catch (IOException | JsonParseException e) {
                    callback.onError(null);
                }
            }
        });
    }

    public void head(String username, File file, Callback<Void> callback) {
        uuid(username, new Callback<String>() {
            @Override
            public void onSuccess(String response) {
                skin(response, new Callback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        getHead(response);
                    }

                    @Override
                    public void onError(ErrorResponse response) {
                        Log.d("kk", "onError: 1");
                        callback.onError(response);
                    }

                    private void getHead(String response) {
                        get(response).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.d("kk", "onError: 2");
                                callback.onError(null);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                try (InputStream is = response.body().byteStream()) {
                                    if (response.code() == 200) {
                                        if (SkinUtils.skinToHeadPng(is, file)) {
                                            callback.onSuccess(null);
                                        } else {
                                            Log.d("kk", "onError: 3");
                                            callback.onError(null);
                                        }
                                    } else {
                                        try (InputStreamReader isr = new InputStreamReader(is)) {
                                            callback.onError(gson.fromJson(isr, ErrorResponse.class));
                                        }
                                    }
                                } catch (IOException e) {
                                    Log.d("kk", "onError: 4");
                                    callback.onError(null);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(ErrorResponse response) {
                Log.d("kk", "onError: 5");
                callback.onError(response);
            }
        });
    }

    public void uuid(String username, Callback<String> callback) {
        get(API_BASE_URL + "/users/profiles/minecraft/" + username).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader reader = response.body().charStream()) {
                    if (response.code() == 200) {
                        callback.onSuccess(JsonParser.parseReader(reader).getAsJsonObject().get("id").getAsString());
                    } else {
                        callback.onError(gson.fromJson(reader, ErrorResponse.class));
                    }
                } catch (IOException e) {
                    callback.onError(null);
                }
            }
        });
    }

    public void versionManifest(Callback<VersionManifest> callback) {
        get(LAUNCHERMETA_BASE_URL + "/mc/game/version_manifest.json").enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader reader = response.body().charStream()) {
                    if (response.code() == 200) {
                        callback.onSuccess(gson.fromJson(reader, VersionManifest.class));
                    } else {
                        callback.onError(gson.fromJson(reader, ErrorResponse.class));
                    }
                } catch (IOException e) {
                    callback.onError(null);
                }
            }
        });
    }

    public void version(VersionManifest.Version version, Callback<Version> callback) {
        get(version.getUrl()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onError(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (Reader reader = response.body().charStream()) {
                    if (response.code() == 200) {
                        callback.onSuccess(gson.fromJson(reader, Version.class));
                    } else {
                        callback.onError(gson.fromJson(reader, ErrorResponse.class));
                    }
                } catch (IOException e) {
                    callback.onError(null);
                }
            }
        });
    }

    private Call get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request);
    }

    private Call postJson(String url, Object body) {
        RequestBody requestBody = RequestBody.create(gson.toJson(body), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    public static MojangRepository getInstance() {
        if (instance == null) {
            synchronized (MojangRepository.class) {
                if (instance == null) {
                    instance = new MojangRepository();
                }
            }
        }
        return instance;
    }

    public interface Callback<T> {
        void onSuccess(T response);

        void onError(ErrorResponse response);
    }
}
