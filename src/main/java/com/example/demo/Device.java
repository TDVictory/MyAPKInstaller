package com.example.demo;

/**
 * Created on 2018/11/14
 */

public class Device {
    private String deviceId;
    private int number;
    private int isOnline;
    private int usbStatus;
    private int storage;
    private int power;
    private int voice;
    private String running;

    public Device(String deviceId, int number) {
        this.deviceId = deviceId;
        this.number = number;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getUsbStatus() {
        return usbStatus;
    }

    public void setUsbStatus(int usbStatus) {
        this.usbStatus = usbStatus;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public String getRunning() {
        return running;
    }

    public void setRunning(String running) {
        this.running = running;
    }
}
