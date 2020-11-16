package com.aof.mcinabox.network.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtractRules {
    private List<String> exclude;

    public ExtractRules() {
        exclude = new ArrayList<>();
    }

    public ExtractRules(String... exclude) {
        if (exclude != null)
            Collections.addAll(this.exclude, exclude);
    }

    public ExtractRules(ExtractRules rules) {
        exclude.addAll(rules.exclude);
    }

    public List<String> getExcludes() {
        return this.exclude;
    }

    public boolean shouldExtract(String path) {
        if (this.exclude != null)
            for (String rule : this.exclude) {
                if (path.startsWith(rule))
                    return false;
            }
        return true;
    }
}
