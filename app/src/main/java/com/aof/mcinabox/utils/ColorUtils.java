package com.aof.mcinabox.utils;

import android.graphics.Color;

/**
 * Created by HaiyuKing
 * Used Color工具类（color整型、rgb数组、16进制互相转换）
 */

public class ColorUtils {

    /**Color的Int整型转Color的16进制颜色值【方案一】
     * colorInt - -12590395
     * return Color的16进制颜色值——#3FE2C5
     * */
    public static String int2Hex(int colorInt){
        String hexCode;
        hexCode = String.format("#%06X", 16777215 & colorInt);
        return hexCode;
    }

    /**Color的Int整型转Color的16进制颜色值【方案二】
     * colorInt - -12590395
     * return Color的16进制颜色值——#3FE2C5
     * */
    public static String int2Hex2(int colorInt){
        String hexCode;
        int[] rgb = int2Rgb(colorInt);
        hexCode = rgb2Hex(rgb);
        return hexCode;
    }

    public static String int2Hex3(int colorInt){
        return "#" +
                intToHex(Color.alpha(colorInt), 2) +
                intToHex(Color.red(colorInt), 2) +
                intToHex(Color.green(colorInt), 2) +
                intToHex(Color.blue(colorInt), 2);
    }

    private static String intToHex(int n,int size) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        a  = add_zore(a,size);
        return a;
    }
    public static String add_zore(String str, int size){
        if (str.length()<size){
            str= "0"+str;
            str=add_zore(str,size);
            return str;
        }else {
            return str;
        }
    }


    /**Color的Int整型转Color的rgb数组
     * colorInt - -12590395
     * return Color的rgb数组 —— [63,226,197]
     * */
    public static int[] int2Rgb(int colorInt){
        int[] rgb = new int[]{0,0,0};

        int red = Color.red(colorInt);
        int green = Color.green(colorInt);
        int blue = Color.blue(colorInt);
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;

        return rgb;
    }

    /**rgb数组转Color的16进制颜色值
     * rgb - rgb数组——[63,226,197]
     * return Color的16进制颜色值——#3FE2C5
     * */
    public static String rgb2Hex(int[] rgb){
        StringBuilder hexCode= new StringBuilder("#");
        for (int value : rgb) {
            int rgbItem = value;
            if (rgbItem < 0) {
                rgbItem = 0;
            } else if (rgbItem > 255) {
                rgbItem = 255;
            }
            String[] code = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
            int lCode = rgbItem / 16;//先获取商，例如，255 / 16 == 15
            int rCode = rgbItem % 16;//再获取余数，例如，255 % 16 == 15
            hexCode.append(code[lCode]).append(code[rCode]);//FF
        }
        return hexCode.toString();
    }
    /**Color的16进制颜色值 转 Color的Int整型
     * colorHex - Color的16进制颜色值——#3FE2C5
     * return colorInt - -12590395
     * */
    public static int hex2Int(String colorHex){
        int colorInt;
        try {
            colorInt = Color.parseColor(colorHex);
        }catch (Exception e){
            e.printStackTrace();
            colorInt = 0;
        }
        return colorInt;
    }

    /**Color的16进制颜色值 转 rgb数组
     * colorHex - Color的16进制颜色值——#3FE2C5
     * return Color的rgb数组 —— [63,226,197]
     * */
    public static int[] hex2Rgb(String colorHex){
        int colorInt = hex2Int(colorHex);
        return int2Rgb(colorInt);
    }

    /**Color的rgb数组转Color的Int整型
     * rgb - Color的rgb数组 —— [63,226,197]
     * return colorInt - -12590395
     * */
    public static int rgb2Int(int[] rgb){
        int colorInt;
        colorInt = Color.rgb(rgb[0],rgb[1],rgb[2]);
        return colorInt;
    }

    public static int[] int2rgba(int colorInt){
        int[] rgba = new int[4];

        int red = Color.red(colorInt);
        int green = Color.green(colorInt);
        int blue = Color.blue(colorInt);
        int alpha = Color.alpha(colorInt);
        rgba[0] = red;
        rgba[1] = green;
        rgba[2] = blue;
        rgba[3] = alpha;

        return rgba;
    }

}
