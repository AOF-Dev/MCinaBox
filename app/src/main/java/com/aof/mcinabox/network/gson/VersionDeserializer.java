package com.aof.mcinabox.network.gson;

import com.aof.mcinabox.network.model.DownloadInfo;
import com.aof.mcinabox.network.model.DownloadType;
import com.aof.mcinabox.network.model.Library;
import com.aof.mcinabox.network.model.ReleaseType;
import com.aof.mcinabox.network.model.Version;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VersionDeserializer implements JsonDeserializer<Version> {
    @Override
    public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject versionJson = json.getAsJsonObject();
        Version version = new Version();

        version.setAssetIndexInfo(context.deserialize(versionJson.get("assetIndex"), Version.AssetIndexInfo.class));
        version.setAssets(versionJson.get("assets").getAsString());
        version.setDownloads(context.deserialize(versionJson.get("downloads"), new TypeToken<Map<DownloadType, DownloadInfo>>() {
        }.getType()));
        version.setId(versionJson.get("id").getAsString());
        version.setLibraries(context.deserialize(versionJson.get("libraries"), Library[].class));
        version.setMainClass(versionJson.get("mainClass").getAsString());
        version.setReleaseTime(context.deserialize(versionJson.get("releaseTime"), Date.class));
        version.setTime(context.deserialize(versionJson.get("time"), Date.class));
        version.setType(context.deserialize(versionJson.get("type"), ReleaseType.class));

        List<String> gameArguments = new ArrayList<>();
        List<String> jvmArguments = new ArrayList<>();
        int minimumLauncherVersion = versionJson.get("minimumLauncherVersion").getAsInt();
        if (minimumLauncherVersion < 21) {
            gameArguments.addAll(Arrays.asList(versionJson.get("minecraftArguments").getAsString().split(" ")));
            gameArguments.add("--width");
            gameArguments.add("${resolution_width}");
            gameArguments.add("--height");
            gameArguments.add("${resolution_height}");

            jvmArguments.add("-Djava.library.path=${natives_directory}");
            jvmArguments.add("-Dminecraft.launcher.brand=${launcher_name}");
            jvmArguments.add("-Dminecraft.launcher.version=${launcher_version}");
            jvmArguments.add("-Dminecraft.client.jar=${primary_jar}");
            jvmArguments.add("-cp");
            jvmArguments.add("${classpath}");
        } else {
            JsonObject argumentsJson = versionJson.get("arguments").getAsJsonObject();

            for (JsonElement gameArgument : argumentsJson.get("game").getAsJsonArray()) {
                if (gameArgument.isJsonPrimitive()) { // Required game argument
                    gameArguments.add(gameArgument.getAsString());
                } else { // Optional game argument
                    JsonObject optionalGameArgument = gameArgument.getAsJsonObject();
                    JsonArray optionalGameArgumentRules = optionalGameArgument.get("rules").getAsJsonArray();

                    JsonObject features = optionalGameArgumentRules.get(0).getAsJsonObject().get("features").getAsJsonObject();
                    if (features.has("has_custom_resolution")
                            && features.get("has_custom_resolution").getAsBoolean()) {
                        for (JsonElement value : optionalGameArgument.get("value").getAsJsonArray()) {
                            gameArguments.add(value.getAsString());
                        }
                    }
                }
            }

            for (JsonElement jvmArgument : argumentsJson.get("jvm").getAsJsonArray()) {
                if (jvmArgument.isJsonPrimitive()) { // Required jvm argument
                    jvmArguments.add(jvmArgument.getAsString());
                } else { // Optional jvm argument
                    // Ignored for now
                }
            }
        }

        version.setGameArguments(gameArguments.toArray(new String[0]));
        version.setJvmArguments(jvmArguments.toArray(new String[0]));

        return version;
    }

    private static class Rule {
        private String action;
        private Os os;

        public String getAction() {
            return action;
        }

        public Os getOs() {
            return os;
        }

        private static class Os {
            private String name;

            public String getName() {
                return name;
            }
        }
    }
}
