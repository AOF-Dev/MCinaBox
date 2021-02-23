package com.aof.mcinabox.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Base64;

import com.aof.mcinabox.MCinaBox;
import com.aof.mcinabox.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class SkinUtils {

    public static Bitmap getUserHead(MCinaBox mCinaBox, String username) {
        File head = mCinaBox.getFileHelper().getHead(username);

        if (!head.exists()) {
            try (InputStream is = mCinaBox.getResources().openRawResource(R.raw.steve)) {
                return skinToHead(is);
            } catch (IOException e) {
                throw new RuntimeException("Failed to open default skin!");
            }
        }

        try (InputStream is = new FileInputStream(head)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            throw new RuntimeException("User skin not valid!");
        }
    }

    public static String getPlayerSkin(Reader reader) {
        try {
            JsonArray properties = JsonParser.parseReader(reader)
                    .getAsJsonObject().getAsJsonArray("properties");
            for (JsonElement property : properties) {
                String skin = getSkinUrlFromProperty(property);
                if (skin != null) return skin;
            }
        } catch (IllegalStateException | NullPointerException ignored) {
        }

        return null;
    }

    private static String getSkinUrlFromProperty(JsonElement property) {
        try {
            JsonObject p = property.getAsJsonObject();
            if (p.get("name").getAsString().equals("textures")) {
                JsonObject value = JsonParser.parseString(
                        new String(Base64.decode(p.get("value").getAsString(), Base64.DEFAULT),
                                StandardCharsets.UTF_8)).getAsJsonObject();
                return value.getAsJsonObject("textures")
                        .getAsJsonObject("SKIN").get("url").getAsString();
            }
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }

        return null;
    }

    public static Bitmap skinToHead(InputStream inputStream) {
        Bitmap bitmap = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Bitmap skinBitmap = BitmapFactory.decodeStream(inputStream);
        Rect head1 = new Rect(8, 8, 16, 16);
        Rect head2 = new Rect(40, 8, 48, 16);
        Rect dst = new Rect(0, 0, 8, 8);
        canvas.drawBitmap(skinBitmap, head1, dst, null);
        canvas.drawBitmap(skinBitmap, head2, dst, null);
        return Bitmap.createScaledBitmap(bitmap, 512, 512, false);
    }

    public static boolean skinToHeadPng(InputStream inputStream, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            skinToHead(inputStream).compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return true;
        } catch (IllegalArgumentException | IOException e) {
            return false;
        }
    }
}
