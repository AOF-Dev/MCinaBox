package com.aof.mcinabox.utils;

public class MathUtils {


    //all arguments must be of the same type
    public static <T extends Comparable<? super T>> T clamp(T value, T min, T max){
        if (value.compareTo(min) < 0) return min;
        if (value.compareTo(max) > 0) return max;
        return value;
    }
}
