package com.example.demo;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2018/11/16
 */
public class HotplugListener implements UsbServicesListener {
    //所有插上USB的设备id均会进入adb列表
    public static Map<String, Device> adb = new ConcurrentHashMap<>();
    //所有插上USB的未认证设备
    public static Map<String, Device> unauthorized = new ConcurrentHashMap<>();
    //所有插上USB的认证设备
    public static Map<String, Device> authorized = new ConcurrentHashMap<>();
    public static Boolean isException = false;
    //当前连上的USB设备
    public static Map<String, Device> deviceLists = new ConcurrentHashMap<>();
    public static Map<String, Device> installLists = new ConcurrentHashMap<>();

    @Override
    public void usbDeviceAttached(UsbServicesEvent usbServicesEvent) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new DevicesThread()).start();
    }


    @Override
    public void usbDeviceDetached(UsbServicesEvent usbServicesEvent) {
        System.out.println("ViveFocus was removed from the bus.");
        new Thread(new DevicesThread()).start();
    }

    // adb和device中有的，更新状态，发给display
    public static void adbAndDeviceList() {
        Set<String> adbs = adb.keySet();
        Set<String> deviceList = deviceLists.keySet();
        Set<String> result = new HashSet<>(adbs);
        result.retainAll(deviceList);
        for (String deviceId : result) {
            Device device = deviceLists.get(deviceId);
            if (device.getUsbStatus() == 0) {
                device.setUsbStatus(1);
            }
        }
    }

    public static void adb(Map<String, Device> map, Map<String, Device> add, String deviceId) {
        if (deviceLists.containsKey(deviceId)) {
            Device device = deviceLists.get(deviceId);
            add.put(deviceId, device);
            map.put(deviceId, device);
        } else {
            Device device = new Device(deviceId, 1);
            add.put(deviceId, device);
            map.put(deviceId, device);
        }
    }

    // add中有，db中没有，把add列表中所有db参数全部移除
    public static Set<String> dbNotIn() {
        Set<String> db = db();
        Set<String> add = add();
        Set<String> result = new HashSet<>(add);
        result.removeAll(db);
        return new HashSet<>(result);
    }

    // remove与deviceList都有的,status置0,发送给display
    public static void removeAndDeviceList() {
        Set<String> deviceList = deviceLists.keySet();
        Set<String> remove = remove();
        Set<String> result = new HashSet<>(deviceList);
        result.retainAll(remove);
        for (String deviceId : result) {
            Device device = deviceLists.get(deviceId);
            if (device.getUsbStatus() == 1) {
                device.setUsbStatus(0);
            }
        }
    }

    // 数据库
    private static Set<String> db() {
        Set<String> strings = new HashSet<>();
//      get from db
//        xxx
        return strings;
    }

    // 取adb列表中有，但deviceList没有的(Add数组)
    public static Set<String> add() {
        Set<String> deviceList = deviceLists.keySet();
        Set<String> adbs = adb.keySet();
        Set<String> result = new HashSet<>(adbs);
        result.removeAll(deviceList);
        return result;
    }

    // 去deviceList有，但adb中没有的（Remove数组）
    private static Set<String> remove() {
        Set<String> deviceList = deviceLists.keySet();
        Set<String> adbs = adb.keySet();
        Set<String> result = new HashSet<>(deviceList);
        result.removeAll(adbs);
        return result;
    }

    // adb中有，但deviceList里online为0,返回adb和online为0的设备ID的交集
    private static Set<String> usbAdd() {
        Map<String, Device> deviceIsOnlineMap = new HashMap<>();
        List<Device> devices = new ArrayList<>(deviceLists.values());
        for (Device device : devices) {
            if (device.getIsOnline() == 0) {
                deviceIsOnlineMap.put(device.getDeviceId(), device);
            }
        }
        Set<String> deviceIsOnline = deviceIsOnlineMap.keySet();
        Set<String> adbs = adb.keySet();
        Set<String> result = new HashSet<>(deviceIsOnline);
        result.retainAll(adbs);
        return result;
    }

    //对于usbAdd数组进行checkIsRunning操作
    public static void checkIsNotOnline() throws IOException {
        //Set<String> strings = usbAdd();
        Set<String> strings = authorized.keySet();
        for (String deviceId : strings) {
            checkIsRunning(deviceId);
        }
    }

    private static void checkIsRunning(String deviceId) throws IOException {
        if (checkService(deviceId)) {
            setWifi(deviceId);
        } else {
            setGroupService(deviceId);
            setGoldfinger(deviceId);
            grandDevice(deviceId);
        }
    }

    //检测Group程序是否运行
    private static boolean checkService(String deviceId) throws IOException {
        String line;
        boolean isStart = false;
        // 只插一台有用
        Process process = Runtime.getRuntime().exec("cmd /c adb -s " + deviceId + " shell dumpsys activity services com.vivedu.groupservice");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        while ((line = bReader.readLine()) != null) {
            if (line.contains("nothing")) {
                isStart = false;
                break;
            } else {
                isStart = true;
            }
        }
        process.destroy();
        bReader.close();
        return isStart;
    }

    //卸载当前设备中的Group程序，如果Group安装路径存在，则安装Group程序
    public static void setGroupService(String deviceId) {
        String separator = File.separator;
        //String groupServicePath = "C:" + separator + "Users" + separator + System.getenv("USERNAME") + separator +
        //        "AppData" + separator + "LocalLow" + separator + "Vivedu" + separator + "GroupSystemServer" + separator
        //        + "core" + separator + "groupservice.apk";
        adb("uninstall com.vivedu.groupservice", deviceId, 1);
        String groupServicePath = groupDefaultPath + "groupservice.apk";
        System.out.println(groupServicePath);
        if (fileExist(groupServicePath)) {
            System.out.println("install groupservice to " + deviceId);
            adb(" -s " + deviceId + " install -r " + groupServicePath, deviceId, 2);
        }
    }

    //卸载当前设备中的Goldfinger程序，如果Goldfinger安装路径存在，则安装Goldfinger程序
    public static void setGoldfinger(String deviceId) {
        String separator = File.separator;
        //String goldFingerPath = "C:" + separator + "Users" + separator + System.getenv("USERNAME") + separator +
        //       "AppData" + separator + "LocalLow" + separator + "Vivedu" + separator + "GroupSystemServer" +
        //        separator + "core" + separator + "goldfinger.apk";
        adb("uninstall com.vivedu.goldfinger", deviceId, 3);
        String goldFingerPath = groupDefaultPath + "goldfinger.apk";
        if (fileExist(goldFingerPath)) {
            System.out.println("install goldfinger to " + deviceId);
            adb(" -s " + deviceId + " install -r " + goldFingerPath, deviceId, 4);
        }
        adb(" -s " + deviceId + " push " + groupDefaultPath + "serverconfig.json " + "/sdcard", deviceId, 5);
    }

    public static void SetInstallWifi(){
        for (String deviceid: installLists.keySet()
             ) {
            try {
                if (checkService(deviceid)){
                    setWifi(deviceid);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setWifi(String deviceId){
//        String wifiName = DemoApplication.aw.GetWifiName();
//        String wifiPass = DemoApplication.aw.GetWifiPass();
//        adb("-s " + deviceId + " shell am startservice --es \"'wifi_name\"' " + wifiName + " --es \"'wifi_password\"' " + wifiPass + " -n com.vivedu.groupservice/.service.WifiAutoConnectService", deviceId, 5);
    }

    //给Group程序授予权限
    public static void grandDevice(String deviceId) {
        System.out.println("grant groupservice.");
        adb("-s " + deviceId + " shell pm grant com.vivedu.groupservice android.permission.READ_EXTERNAL_STORAGE", deviceId, 6);
        adb("-s " + deviceId + " shell pm grant com.vivedu.groupservice android.permission.WRITE_EXTERNAL_STORAGE", deviceId, 7);
        adb("-s " + deviceId + " shell pm grant com.vivedu.groupservice android.permission.PACKAGE_USAGE_STATS", deviceId, 8);
        adb("-s " + deviceId + " shell settings put secure enabled_accessibility_services com.vivedu.groupservice/com.vivedu.groupservice.service.ApkInstallAndUninstallService", deviceId, 9);
        adb("-s " + deviceId + " shell settings put secure accessibility_enabled 1", deviceId, 10);
        adb("-s " + deviceId + " shell settings put secure install_non_market_apps 1", deviceId, 11);
        adb("-s " + deviceId + " shell am start -n com.vivedu.groupservice/.activity.LauncherActivity", deviceId, 12);
//        adb("-s " + deviceId + " shell dpm set-active-admin --user current com.vivedu.groupservice/.service.ScreenOffAdminReceiver", deviceId, 13);
    }

    //执行adb指令
    public static void adb(String adb, String deviceId, int count) {
        String line;
        try {
            Process process = Runtime.getRuntime().exec("cmd /c "+ adbPath + " " + adb);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            while ((line = bReader.readLine()) != null) {
                System.out.println(line);
                if (!line.equals("Success") && !line.startsWith("Starting")) {
                    isException = true;
                }
            }
            System.out.println("Process is Running");
            process.waitFor();
            System.out.println("Process is Ending");
            SetProgress(count);
            process.destroy();
            bReader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            isException = true;
        }
    }

    private static void SetProgress(int count){
        System.out.println(count);
        DemoApplication.aw.SetProgress();
    }

    private static boolean fileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void AddInstallList(String deviceID){
        if (!authorized.containsKey(deviceID)) {
            return;
        }
        installLists.put(deviceID,authorized.get(deviceID));
    }

    public static void RemoveInstallList(String deviceID){
        if (!installLists.containsKey(deviceID)){
            return;
        }
        installLists.remove(deviceID);
    }

    public static void InstallAll(){
        Set<String> strings = installLists.keySet();
        String separator = File.separator;
        for (String deviceId : strings) {
            for (String apkName: GetAPKName(FilePath() + "APK")
            ) {
                if (apkName.endsWith(".apk")){
                    String ApkPath = FilePath() + "APK" + separator + apkName;
                    System.out.println(ApkPath);
                    APKInstallThread apkInstallThread = new APKInstallThread(deviceId,ApkPath);
                    apkInstallThread.start();
                }
            }
        }
    }

    //安装相对路径下的指定APK
    public static void InstallAPK(String deviceId){
        String separator = File.separator;
        for (String apkName: GetAPKName(FilePath() + "APK")
             ) {
            if (apkName.endsWith(".apk")){
                String ApkPath = FilePath() + "APK" + separator + apkName;
                System.out.println(ApkPath);
                APKInstallThread apkInstallThread = new APKInstallThread(deviceId,ApkPath);
                apkInstallThread.start();
            }
        }

    }


    public static String[] GetAPKName(String ApkPath){
        File myFile = new File(ApkPath);
        String[] fileList = myFile.list();
        DemoApplication.aw.SetProgressMaxValue(fileList.length * installLists.size());
        return  fileList;
    }

    public static String FilePath(){
        String path = System.getProperty("java.class.path");
        int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
        int lastIndex = path.lastIndexOf(File.separator) + 1;
        path = path.substring(firstIndex, lastIndex);
        return path;
    }

    public static String groupDefaultPath = FilePath() + "GroupDefault" + File.separator;
    public static String adbPath = FilePath() + "adb" + File.separator + "adb.exe";

}