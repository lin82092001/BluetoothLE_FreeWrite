package com.by.bledemo;

/**
 * Created by LinLin on 2018/1/31.
 */

public class SensorData {
    private float Roll_left,Roll_right;
    private float Pitch_left,Pitch_right;
    private float Yaw_left,Yaw_right;
    private float AccXL;
    private float AccYL;
    private float AccZL;
    private float AccXR;
    private float AccYR;
    private float AccZR;

    public SensorData(float AccXL, float AccYL, float AccZL,float AccXR, float AccYR, float AccZR)
    {
        this.AccXL=AccXL;
        this.AccYL=AccYL;
        this.AccZL=AccZL;
        this.AccXR=AccXR;
        this.AccYR=AccYR;
        this.AccZR=AccZR;
    }

    public void setLeftEuler(float Roll, float Pitch, float Yaw)
    {
        Roll_left=Roll;
        Pitch_left=Pitch;
        Yaw_left=Yaw;
    }
    public void setRightEuler(float Roll, float Pitch, float Yaw)
    {
        Roll_right=Roll;
        Pitch_right=Pitch;
        Yaw_right=Yaw;
    }
    public void setAccXL(float input)
    {
        AccXL=input;
    }
    public void setAccYL(float input)
    {
        AccYL=input;
    }
    public void setAccZL(float input)
    {
        AccZL=input;
    }
    public void setAccXR(float input)
    {
        AccXR=input;
    }
    public void setAccYR(float input)
    {
        AccYR=input;
    }
    public void setAccZR(float input)
    {
        AccZR=input;
    }
    public float getAccXL()
    {
        return AccXL;
    }
    public float getAccYL()
    {
        return AccYL;
    }
    public float getAccZL()
    {
        return AccZL;
    }
    public float getAccXR()
    {
        return AccXR;
    }
    public float getAccYR()
    {
        return AccYR;
    }
    public float getAccZR()
    {
        return AccZR;
    }
}
