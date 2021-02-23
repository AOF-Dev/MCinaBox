package com.aof.mcinabox.network.model;

import java.util.Map;

public class CompatibilityRule {
    public enum Action {
        ALLOW, DISALLOW
    }

    public interface FeatureMatcher {
        boolean hasFeature(String param1String, Object param1Object);
    }

    public class OSRestriction {
        private OperatingSystem name;

        public OperatingSystem getName() {
            return this.name;
        }

        public boolean isCurrentOperatingSystem() {
            return this.name == null || this.name == OperatingSystem.LINUX;
        }
    }

    private final Action action;
    private OSRestriction os;
    private Map<String, Object> features;

    public CompatibilityRule() {
        this.action = Action.ALLOW;
    }

    public Action getAppliedAction(FeatureMatcher featureMatcher) {
        if (this.os != null && !this.os.isCurrentOperatingSystem())
            return null;
        if (this.features != null) {
            if (featureMatcher == null)
                return null;
            for (Map.Entry<String, Object> feature : this.features.entrySet()) {
                if (!featureMatcher.hasFeature(feature.getKey(), feature.getValue()))
                    return null;
            }
        }
        return this.action;
    }

    public Action getAction() {
        return this.action;
    }

    public OSRestriction getOs() {
        return this.os;
    }

    public Map<String, Object> getFeatures() {
        return this.features;
    }
}
