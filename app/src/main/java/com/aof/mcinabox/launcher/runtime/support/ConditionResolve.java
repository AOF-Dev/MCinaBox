package com.aof.mcinabox.launcher.runtime.support;

import java.util.ArrayList;

public class ConditionResolve {

    public static boolean handleConditionWithLauncherVersion(int launcherversion, String condition){
        //>0 <1|>2 <3
        String[] conditions = condition.split(Definitions.RUNTIME_CONDITION_SPILT);
        for(String str : conditions){
            if(checkConditionWithLauncherVersion(launcherversion,str)){
                return true;
            }
        }
        return false;
    }

    private static boolean checkConditionWithLauncherVersion(int launcherversion, String handledcondition){
        boolean result = true;
        for(int a = 0; a < handledcondition.length(); a++){
            if(handledcondition.charAt(a) == '<'){
                ArrayList<String> nums = new ArrayList<>();
                for(int b = a + 1; b < handledcondition.length(); b++){
                    if(handledcondition.charAt(b) != ' '){
                        nums.add(String.valueOf(handledcondition.charAt(b)));
                    }
                }
                StringBuilder numstr = new StringBuilder();
                for(String str : nums){
                    numstr.append(str);
                }

                int num = Integer.parseInt(numstr.toString());
                if(launcherversion >= num){
                    result = false;
                }
            }
            if(handledcondition.charAt(a) == '>'){
                ArrayList<String> nums = new ArrayList<>();
                for(int b = a + 1; b < handledcondition.length(); b++){
                    if(handledcondition.charAt(b) != ' '){
                        nums.add(String.valueOf(handledcondition.charAt(b)));
                    }
                }
                StringBuilder numstr = new StringBuilder();
                for(String str : nums){
                    numstr.append(str);
                }

                int num = Integer.parseInt(numstr.toString());
                if(launcherversion <= num){
                    result = false;
                }
            }
        }
        return result;
    }

    public static boolean handleConditionWithMinecraftVersion(String versionId, String condition){
        String[] conditions = condition.split(Definitions.RUNTIME_CONDITION_SPILT);
        for(String str : conditions){
            if(str.equals(versionId)){
                return true;
            }
        }
        return false;
    }

}
