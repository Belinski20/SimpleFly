package com.belinski20.simplefly;

public class PlayerData {
    private long flyTime;
    private boolean isFlying;

    public PlayerData(long flyTime)
    {
        this.flyTime = flyTime;
        isFlying = true;
    }

    public long getFlyTime()
    {
        return flyTime;
    }

    public void decrementTime()
    {
        this.flyTime--;
    }

    public void incrementTime(int timeInMinutes)
    {
        long seconds = timeInMinutes * 60;
        this.flyTime += seconds;
    }

    public boolean isFlying()
    {
        return isFlying;
    }

    public void setFlying(boolean isFlying)
    {
        this.isFlying = isFlying;
    }
}
