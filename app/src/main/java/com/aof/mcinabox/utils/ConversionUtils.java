package com.aof.mcinabox.utils;

import static androidx.core.math.MathUtils.clamp;

public class ConversionUtils {

    /*
    Created by longjunyu2
    2020/09/18

    Tweaked by serpentspirale
    2020/12/26
     */

    /**[容量换算方法]**/
    public final static int CAPACITY_TYPE_BYTE = 0;
    public final static int CAPACITY_TYPE_KBYTE = 1;
    public final static int CAPACITY_TYPE_MBYTE = 2;
    public final static int CAPACITY_TYPE_GBYTE = 3;

    public static float capacityConvert(int originalType, float originalValue, int targetType){
        return convert(originalValue, Math.abs(targetType - originalType), clamp(targetType - originalType, 0, 1));
    }

    private static float convert(float valueToConvert, int numberOfConversions, int directionOfConversion){
        switch (directionOfConversion){

            case 0: //Convert to a smaller unit
                for(int a = 0; a < numberOfConversions; a ++){
                    valueToConvert *= 1024;
                }
                break;

            case 1: //Convert to a bigger unit
                for(int a = 0; a < numberOfConversions; a ++){
                    valueToConvert /= 1024;
                }
                break;
        }
        return valueToConvert;
    }




}
