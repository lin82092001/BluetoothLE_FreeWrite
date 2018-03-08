package com.by.bledemo.DataProcess;

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
    private String Address;
    private int[][] Fingers;

    public SensorData(float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ, int[][] Fingers, String Address)
    {
        this.Roll=Roll;
        this.Pitch=Pitch;
        this.Yaw=Yaw;
        this.AccX=AccX;
        this.AccY=AccY;
        this.AccZ=AccZ;
        this.Address=Address;
        this.Fingers=Fingers;
    }

    public void setEuler(float Roll, float Pitch, float Yaw, String inputAdd)
    {
        this.Roll=Roll;
        this.Pitch=Pitch;
        this.Yaw=Yaw;
        this.Address=inputAdd;
    }
    public void setAcc(float inputX,float inputY,float inputZ, String inputAdd)
    {
        AccX=inputX;
        AccY=inputY;
        AccZ=inputZ;
        Address=inputAdd;
    }
    public void setFigs(int[][] inputFigs)
    {
        Fingers=inputFigs;
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
    public String getAddress()
    {
        return Address;
    }
}
