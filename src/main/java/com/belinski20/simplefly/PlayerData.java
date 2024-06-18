package com.belinski20.simplefly;

public class PlayerData {
    private long flyTime;

    private boolean isFlying;

    public PlayerData(long flyTime) {
        this.flyTime = flyTime;
        this.isFlying = true;
    }

    public long getFlyTime() {
        return this.flyTime;
    }

    public void decrementTime() {
        this.flyTime--;
    }

    public void incrementTime(int timeInMinutes) {
        long seconds = (timeInMinutes * 60L);
        this.flyTime += seconds;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public void setFlying(boolean isFlying) {
        this.isFlying = isFlying;
    }
}
