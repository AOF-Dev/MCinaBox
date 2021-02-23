package com.aof.mcinabox.model;

import java.util.Map;

public class ArgumentsSubstitutor {
    private final Map<String, String> map;

    public ArgumentsSubstitutor(Map<String, String> map) {
        this.map = map;
    }

    public String[] substitute(String[] arguments) {
        String[] substitutedArguments = new String[arguments.length];
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value == null) value = "";
            String toSubstitute = "${" + key + "}";
            for (int i = 0; i < arguments.length; i++) {
                substitutedArguments[i] = arguments[i].replace(toSubstitute, value);
            }
        }
        return substitutedArguments;
    }
}
