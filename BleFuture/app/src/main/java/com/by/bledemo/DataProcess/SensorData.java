package com.by.bledemo.DataProcess;

import com.by.bledemo.Controller.Controller;

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
    private int Status;
    private int CMD;
    private String Address;
    private Controller.FingersStatus Fingers;

    public SensorData(int Status,int CMD,float Roll, float Pitch, float Yaw,float AccX, float AccY, float AccZ, Controller.FingersStatus Fingers, String Address)
    {
        this.Status=Status;
        this.CMD=CMD;
        this.Roll=Roll;
        this.Pitch=Pitch;
        this.Yaw=Yaw;
        this.AccX=AccX;
        this.AccY=AccY;
        this.AccZ=AccZ;
        this.Fingers=Fingers;
        this.Address=Address;
    }

    //Setter
    public void setEuler(int Status,int CMD,float Roll, float Pitch, float Yaw, String inputAdd)
    {
        this.Status=Status;
        this.CMD=CMD;
        this.Roll=Roll;
        this.Pitch=Pitch;
        this.Yaw=Yaw;
        this.Address=inputAdd;
    }
    public void setAcc(float inputX,float inputY,float inputZ, String inputAdd)
    {
        this.AccX=inputX;
        this.AccY=inputY;
        this.AccZ=inputZ;
        this.Address=inputAdd;
    }
    public void setFigs(Controller.FingersStatus inputFigs, String inputAdd)
    {
        this.Fingers=inputFigs;
        this.Address=inputAdd;
    }

    //Getter
    public int getStatus()
    {
        return Status;
    }
    public int getCMD()
    {
        return CMD;
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
    public Controller.FingersStatus getFingers()
    {
        return Fingers;
    }
}
