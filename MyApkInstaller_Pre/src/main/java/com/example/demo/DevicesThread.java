package com.example.demo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Created on 2018/12/24 0024
 */

public class DevicesThread implements Runnable {

    @Override
    public void run() {
        //清空所有的状态列表
        HotplugListener.adb.clear();
        HotplugListener.unauthorized.clear();
        HotplugListener.authorized.clear();
        HotplugListener.installLists.clear();


        try {
            String line;
            Process process = Runtime.getRuntime().exec("cmd /c " +HotplugListener.adbPath + " devices");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            //根据adb读取的指令设置当前机器是否对该电脑进行了调试授权
            while ((line = bReader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("\tdevice")) {
                    String deviceId = line.replace("\tdevice", "");
                    HotplugListener.adb(HotplugListener.authorized, HotplugListener.adb, deviceId);
                } else if (line.contains("\tunauthorized")) {
                    String deviceId = line.replace("\tunauthorized", "");
                    HotplugListener.adb(HotplugListener.unauthorized, HotplugListener.adb, deviceId);
                }
            }
            //adb进程销毁，关闭读取
            process.destroy();
            bReader.close();
            //添加所有需要解决问题的设备ID（包含未授权以及db中不存在的设备）
            //Set<String> deal = HotplugListener.unauthorized.keySet();
            //Set<String> deals = new HashSet<>();
            //deals.addAll(deal);
            //deals.addAll(HotplugListener.dbNotIn());


            // 通知usb状态变化
            //for (String deviceId : HotplugListener.adb.keySet()) {
//                HotplugListener.setUSB(deviceId);
            //}

//            HotplugListener.checkIsNotOnline();
//            HotplugListener.active(deals);
            //HotplugListener.adbAndDeviceList();
            //HotplugListener.removeAndDeviceList();
            //显示所有连接的设备
            DemoApplication.aw.ShowAllDeviceID();
            //所有授权设备添加至安装列表
            HotplugListener.installLists.putAll(HotplugListener.authorized);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}