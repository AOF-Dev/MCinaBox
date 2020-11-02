package com.aof.mcinabox.utils;

public class ConversionUtils {

    /*
    Create by longjunyu2
    2020/09/18
     */

    /**[容量换算方法]**/
    public final static int CAPACITY_TYPE_BYTE = 0;
    public final static int CAPACITY_TYPE_KBYTE = 1;
    public final static int CAPACITY_TYPE_MBYTE = 2;
    public final static int CAPACITY_TYPE_GBYTE = 3;

    public static float capacityConvert(int originalType, float original, int targetType){

        class C{
            public float c1(float i, int t, int m){
                switch (m){
                    case 0:
                        for(int a = 0; a < t; a ++){
                            i *= 1024;
                        }
                        break;
                    case 1:
                        for(int a = 0; a < t; a ++){
                            i /= 1024;
                        }
                        break;
                }
                return i;
            }
        }
        //升量转换
        if( (originalType == CAPACITY_TYPE_BYTE && targetType == CAPACITY_TYPE_KBYTE) || (originalType == CAPACITY_TYPE_KBYTE && targetType == CAPACITY_TYPE_MBYTE)
            || (originalType == CAPACITY_TYPE_MBYTE && targetType == CAPACITY_TYPE_GBYTE)){
            return new C().c1(original,1,0);
        }

        if ( (originalType == CAPACITY_TYPE_BYTE && targetType == CAPACITY_TYPE_MBYTE) || (originalType == CAPACITY_TYPE_KBYTE && targetType == CAPACITY_TYPE_GBYTE)){
            return new C().c1(original, 2, 0);
        }

        if ( originalType == CAPACITY_TYPE_BYTE && targetType == CAPACITY_TYPE_GBYTE ){
            return new C().c1(original,3,0);
        }

        //降量转换
        if( (targetType == CAPACITY_TYPE_BYTE && originalType == CAPACITY_TYPE_KBYTE) || (targetType == CAPACITY_TYPE_KBYTE && originalType == CAPACITY_TYPE_MBYTE)
                || (targetType == CAPACITY_TYPE_MBYTE && originalType == CAPACITY_TYPE_GBYTE)){
            return new C().c1(original,1,1);
        }

        if ( (targetType == CAPACITY_TYPE_BYTE && originalType == CAPACITY_TYPE_MBYTE) || (targetType == CAPACITY_TYPE_KBYTE && originalType == CAPACITY_TYPE_GBYTE)){
            return new C().c1(original, 2, 1);
        }

        if ( targetType == CAPACITY_TYPE_BYTE && originalType == CAPACITY_TYPE_GBYTE ){
            return new C().c1(original,3,1);
        }

        return 0;
    }

}
