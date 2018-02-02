package com.by.bledemo;

/**
 * Created by LinLin on 2018/1/31.
 */

public class SensorData {
    private float Roll;
    private float Pitch;
    private float Yaw;
    private float AccX;
    private float AccY;
    private float AccZ;

    public SensorData(float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ)
    {
        this.Roll=Roll;
        this.Pitch=Pitch;
        this.Yaw=Yaw;
        this.AccX=AccX;
        this.AccY=AccY;
        this.AccZ=AccZ;
    }

    public void setEuler(float Roll, float Pitch, float Yaw)
    {
        this.Roll=Roll;
        this.Pitch=Pitch;
        this.Yaw=Yaw;
    }
    public void setAcc(float inputX,float inputY,float inputZ)
    {
        AccX=inputX;
        AccY=inputY;
        AccZ=inputZ;
    }
    public float getAccX()
    {
        return AccX;
    }
    public float getAccY()
    {
        return AccY;
    }
    public float getAccZ()
    {
        return AccZ;
    }
    public float getRoll()
    {
        return Roll;
    }
    public float getPitch()
    {
        return Pitch;
    }
    public float getYaw()
    {
        return Yaw;
    }
}
