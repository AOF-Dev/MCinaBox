package com.aof.sharedmodule.Tools;

import android.util.Log;

import java.util.HashMap;

public class Convert_GLFW_LWJGL {

    private HashMap<Integer,Integer> ConvertMap;

    public Convert_GLFW_LWJGL(){
        super();
        ConvertMap = new HashMap<Integer, Integer>();
        putin(new int[][]{
                //Num 0~9
                {48,11}, //Num 0
                {49,2}, //Num 1
                {50,3}, //Num 2
                {51,4}, //Num 3
                {52,5}, //Num 4
                {53,6}, //Num 5
                {54,7}, //Num 6
                {55,8}, //Num 7
                {56,9}, //Num 8
                {57,10}, //Num 9

                //Eg A~Z
                {65,30}, //A
                {66,48}, //B
                {67,46}, //C
                {68,32}, //D
                {69,18}, //E
                {70,33}, //F
                {71,34}, //G
                {72,35}, //H
                {73,23}, //I
                {74,36}, //J
                {75,37}, //K
                {76,38}, //L
                {77,50}, //M
                {78,49}, //N
                {79,24}, //O
                {80,25}, //P
                {81,16}, //Q
                {82,19}, //R
                {83,31}, //S
                {84,20}, //T
                {85,22}, //U
                {86,47}, //V
                {87,17}, //W
                {88,45}, //X
                {89,21}, //Y
                {90,44}, //Z

                //F1~F12
                {290,59}, //F1
                {291,60}, //F2
                {292,61}, //F3
                {293,62}, //F4
                {294,63}, //F5
                {295,64}, //F6
                {296,65}, //F7
                {297,66}, //F8
                {298,67}, //F9
                {299,68}, //F10
                {300,87}, //F11
                {301,88}, //F12

                //Other
                {256,1}, //ESC
                {259,14}, //Backspace
                {258,15}, //TAB
                {341,29}, //Lctrl
                {340,42}, //Lshift
                {344,54}, //Rshift
                {32,57}, //space
                {346,184}, //Ralt
                {265,200}, //Up
                {264,208}, //Down
                {263,203}, //Left
                {262,205}, //Right
                {268,199}, //Home
                {269,207}, //End
                {266,201}, //pageup
                {267,209}, //pagedown
                {261,211}, //delete
                {260,210}, //insert
                {283,183}, //prt
                {284,197}, //Pause
                {257,28}, //Enter
                {280,58}, //CAPLock
                {345,157}, //Rctrl
                {342,56}, //Lalt
                {1001,1001}, //MOUSE_Pri
                {1002,1002}, //MOUSE_Sec

        });
    }

    public int GetLwjglInputFromGLFW(int glfw_input) {

        if(ConvertMap.containsKey(glfw_input)){
            return ConvertMap.get(glfw_input);
        }else{
            Log.e("Convert_GLFW_LWJGL","Can't convert GLFW_INPUT :" + glfw_input);
            return -1;
        }

    }

    public byte GetMouseFromGLFW(byte glfw_input){

        switch(glfw_input){
            case 1:
                return 1;
            case 3:
                return 2;
            default:
                Log.e("Convert_GLFW_LWJGL","Can't convert MOUSE_INPUT :" + glfw_input);
                return -1;
        }

    }

    private void putin(int[][] in){
        for(int[] tmp:in){
            ConvertMap.put(tmp[0],tmp[1]);
        }
    }
}
