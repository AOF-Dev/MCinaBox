package cosine.boat2;

import java.util.*;

import com.google.gson.*;

import java.io.*;

import android.util.*;

public class Config extends HashMap<String, String> {

    public static Config getConfig(String cfgFile) {
        try {
            return new Gson().fromJson(new String(Utils.readFile(cfgFile), "UTF-8"), Config.class);
        } catch (JsonSyntaxException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String get(String key) {
        String value;

        if (super.get(key) == null) {
            value = "";
            Log.w("Boat", "Value required can not found: key=" + key);
        } else {
            value = super.get(key);
        }
        return value;
    }

}
