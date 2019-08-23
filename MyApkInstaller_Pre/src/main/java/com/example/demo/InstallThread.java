package com.example.demo;
public class InstallThread extends Thread {
	private String deviceID;
	public InstallThread(String DeviceID) {
		deviceID = DeviceID;
	}
	@Override
	public void run() {
		/*
			HotplugListener.setGroupService(deviceID);
			HotplugListener.setGoldfinger(deviceID);
			HotplugListener.grandDevice(deviceID);
		*/
		//System.out.println("安装APK");
		HotplugListener.InstallAPK(deviceID);
	}
	
}
