package com.aof.mcinabox.network.model;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;

public class Library {
    private String name;
    private List<CompatibilityRule> rules;
    private Map<OperatingSystem, String> natives;
    private ExtractRules extract;
    private String url;
    private LibraryDownloadInfo downloads;

    public String getName() {
        return name;
    }

    public List<CompatibilityRule> getCompatibilityRules() {
        return rules;
    }

    public boolean appliesToCurrentEnvironment(CompatibilityRule.FeatureMatcher featureMatcher) {
        if (rules == null)
            return true;
        CompatibilityRule.Action lastAction = CompatibilityRule.Action.DISALLOW;
        for (CompatibilityRule compatibilityRule : rules) {
            CompatibilityRule.Action action = compatibilityRule.getAppliedAction(featureMatcher);
            if (action != null)
                lastAction = action;
        }
        return (lastAction == CompatibilityRule.Action.ALLOW);
    }

    public Map<OperatingSystem, String> getNatives() {
        return natives;
    }

    public ExtractRules getExtractRules() {
        return extract;
    }

    public String getArtifactBaseDir() {
        if (name == null)
            throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
        String[] parts = name.split(":", 3);
        return String.format("%s/%s/%s", parts[0].replaceAll("\\.", "/"), parts[1], parts[2]);
    }

    public String getArtifactPath() {
        return getArtifactPath(null);
    }

    public String getArtifactPath(String classifier) {
        if (name == null)
            throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
        return String.format("%s/%s", getArtifactBaseDir(), getArtifactFilename(classifier));
    }

    public String getArtifactFilename(String classifier) {
        if (name == null)
            throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
        String[] parts = name.split(":", 3);
        return String.format("%s-%s%s.jar", parts[1], parts[2], TextUtils.isEmpty(classifier) ? "" : ("-" + classifier));
    }
}
