package com.example.demo;

public class APKInstallThread extends Thread {
    String deviceID;
    String apkPath;
    public APKInstallThread(String DeviceID,String APKPath) {
        deviceID = DeviceID;
        apkPath = APKPath;
    }

    @Override
    public void run() {
        System.out.println("Thread" + deviceID + " is installing apk: " + apkPath);
        HotplugListener.adb(" -s " + deviceID + " install -r " + apkPath, deviceID, 12);
    }
}
