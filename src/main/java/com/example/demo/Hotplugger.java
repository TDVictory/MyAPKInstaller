package com.example.demo;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created on 2018/11/16
 */
public class Hotplugger extends Thread {
    public void run(){
        UsbServices services = null;



        try {
            services = UsbHostManager.getUsbServices();
        } catch (UsbException e) {
            e.printStackTrace();
        }
        assert services != null;
        services.addUsbServicesListener(new HotplugListener());
    }

}
