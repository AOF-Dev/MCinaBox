package com.aof.mcinabox.utils;

public class FormatUtils {

    /*
    Create by longjunyu2
    2020/09/18
    */

    public final static int CAPACITY_TYPE_BYTE = 0;
    public final static int CAPACITY_TYPE_KBYTE = 1;
    public final static int CAPACITY_TYPE_MBYTE = 2;
    public final static int CAPACITY_TYPE_GBYTE = 3;

    /**[容量格式化]**/
    public static String formatCapacity(float capacity, int type){
        String suffix;
        switch (type){
            case CAPACITY_TYPE_BYTE:
                suffix = "B";
                break;
            case CAPACITY_TYPE_KBYTE:
                suffix = "KB";
                break;
            case CAPACITY_TYPE_MBYTE:
                 suffix = "MB";
                 break;
            case CAPACITY_TYPE_GBYTE:
                suffix = "GB";
                break;
            default:
                suffix = "";
        }
        return capacity + " " + suffix;
    }

    public final static int DTS_TYPE_MS = 0;
    public final static int DTS_TYPE_S = 1;
    public final static int DTS_TYPE_MIN = 2;
    /**[数据传输速度格式化]**/
    public static String formatDataTransferSpeed(float capacity, int capacityType, int timeType){
        String i = formatCapacity(capacity,capacityType);
        String suffix;
        switch (timeType){
            case DTS_TYPE_MS:
                suffix = "/ms";
                break;
            case DTS_TYPE_S:
                suffix = "/s";
                break;
            case DTS_TYPE_MIN:
                suffix = "/min";
                break;
            default:
                suffix = "";
        }
        return i + suffix;
    }

}
