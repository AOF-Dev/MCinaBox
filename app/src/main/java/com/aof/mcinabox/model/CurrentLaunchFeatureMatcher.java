package com.aof.mcinabox.model;

import com.aof.mcinabox.network.model.CompatibilityRule;
import com.google.common.base.Objects;

public class CurrentLaunchFeatureMatcher implements CompatibilityRule.FeatureMatcher {
  
  public boolean hasFeature(String name, Object value) {
    if (name.equals("is_demo_user"))
      return Objects.equal(false, value);
    if (name.equals("has_custom_resolution"))
      return Objects.equal(true, value);
    return false;
  }
}
