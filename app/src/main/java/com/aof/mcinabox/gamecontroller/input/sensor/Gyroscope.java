package com.aof.mcinabox.gamecontroller.input.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.input.Input;

public class Gyroscope implements Input, SensorEventListener {

    private final static String TAG = "Gyroscope";

    private Context mContext;
    private Controller mController;
    private Sensor mSensor;
    private SensorManager mSensorManager;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        this.mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        this.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //设置监听器
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //TODO:完成陀螺仪控制视角移动的算法

        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {

    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void setEnable(boolean enable) {
        //TODO:设置启用或禁用
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

        float[] remappedRotationMatrix = new float[16];
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);

        float[] orientations = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientations);

        for (int i = 0; i < 3; i++) {
            orientations[i] = (float) (Math.toDegrees(orientations[i]));
        }
        //orientations的三个元素 分别为 单位时间内手机沿Y,Z,X偏转的角度
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //to do nothing.
    }
}
