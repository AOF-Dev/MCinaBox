package com.aof.mcinabox.network.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {
    private final DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive))
            throw new JsonParseException("The date should be a string value");

        if (typeOfT == Date.class) {
            try {
                return iso8601Format.parse(json.getAsString());
            } catch (ParseException ignored) {
            }
        }

        throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
    }

    public String serializeToString(Date date) {
        String result = iso8601Format.format(date);
        return result.substring(0, 22) + ":" + result.substring(22);
    }
}
