package com.aof.mcinabox.network.gson;

import com.aof.mcinabox.network.model.ReleaseType;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ReleaseTypeAdapterFactory extends TypeAdapter<ReleaseType> {

    @Override
    public void write(JsonWriter out, ReleaseType value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public ReleaseType read(JsonReader in) throws IOException {
        return ReleaseType.getByName(in.nextString());
    }
}
