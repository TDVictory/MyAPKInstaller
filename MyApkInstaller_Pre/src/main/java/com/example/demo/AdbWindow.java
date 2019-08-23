package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AdbWindow extends JFrame {
    private static final int MIN_PROGRESS = 0;
    private static int MAX_PROGRESS = 12;
    private static int curProgress;


    JFrame f;
    JPanel contentPane;
    JPanel devicePanel;
    JLabel l;
    //JTextField APKName;
    /*
    JTextField wifiName;
    JTextField wifiPass;
    JLabel installInfo;
    JButton setWIFIButton;
    */
    JButton installButton;
    JButton refleshButton;

    JProgressBar progressBar;

    public AdbWindow(){
        f=new JFrame("ADB测试窗体！");
        contentPane = new JPanel();
        contentPane.setLayout(null);
        //设置放置设备的窗体
        devicePanel = new JPanel();
        devicePanel.setLocation(10,50);
        devicePanel.setSize(500,420);
        devicePanel.setBorder(BorderFactory.createLoweredBevelBorder());
        contentPane.add(devicePanel);
        //JLable组件
        l=new JLabel("USB连接设备：");
        l.setFont(new Font("宋体",Font.BOLD,20));//设置字体大小格式
        l.setForeground(Color.black);//设置颜色
        l.setSize(300, 20);
        l.setLocation(10,20);
        contentPane.add(l);
/*
        //APK安装组件
        JLabel nameLabel = new JLabel("APKName");
        nameLabel.setLocation(550,270);
        nameLabel.setSize(100,20);
        contentPane.add(nameLabel);
        APKName = new JTextField();
        APKName.setLocation(550,300);
        APKName.setSize(200,30);
        contentPane.add(APKName);
        */
        /*
        //WifiName 组件
        JLabel nameLabel = new JLabel("WifiName");
        nameLabel.setLocation(550,270);
        nameLabel.setSize(100,20);
        contentPane.add(nameLabel);
        wifiName = new JTextField();
        wifiName.setLocation(550,300);
        wifiName.setSize(200,30);
        contentPane.add(wifiName);
        //WifiPass 组件
        JLabel passLabel = new JLabel("WifiPass");
        passLabel.setLocation(550,330);
        passLabel.setSize(100,20);
        contentPane.add(passLabel);
        wifiPass = new JTextField();
        wifiPass.setLocation(550,360);
        wifiPass.setSize(200,30);
        contentPane.add(wifiPass);
        */
        //安装按钮 组件
        installButton = new JButton("开始安装");
        installButton.setSize(120,30);
        installButton.setLocation(650,500);
        installButton.addActionListener(new OnInstallButtonClick());
        contentPane.add(installButton);
        //刷新按钮 组件
        refleshButton = new JButton("刷新状态");
        refleshButton.setSize(120,30);
        refleshButton.setLocation(500,500);
        refleshButton.addActionListener(new OnRefleshButtonClick());
        contentPane.add(refleshButton);

        /*
        //设置WIFI按钮 组件
        setWIFIButton = new JButton("设置WIFI");
        setWIFIButton.setSize(120,30);
        setWIFIButton.setLocation(650,450);
        setWIFIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HotplugListener.SetInstallWifi();
            }
        });
        contentPane.add(setWIFIButton);
        */

        //JProgressBar
        progressBar = new JProgressBar();
        progressBar.setLocation(550,400);
        progressBar.setSize(180,10);
        // 设置进度的 最小值 和 最大值
        progressBar.setMinimum(MIN_PROGRESS);
        progressBar.setMaximum(MAX_PROGRESS);
        curProgress = MIN_PROGRESS;
        // 设置当前进度值
        progressBar.setValue(curProgress);

        // 绘制百分比文本（进度条中间显示的百分数）
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        contentPane.add(progressBar);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800,600);//设置窗口大小
        f.setResizable(false);
        f.setContentPane(contentPane);
        f.setVisible(true);//显示窗口
    }

    public void ShowAllDeviceID(){
        devicePanel.removeAll();
        System.out.println("重新绘制，此时认证列表为：");
        // 创建复选框
        //for (int j = 0; j < 10; j++) {
            for (String info:HotplugListener.authorized.keySet()
            ) {
                System.out.println(info);
                JCheckBox checkBox01 = new JCheckBox(info);
                checkBox01.setSelected(true);
                checkBox01.addActionListener(new CheckBoxTest());
                devicePanel.add(checkBox01);
            }
            for (String uaInfo:HotplugListener.unauthorized.keySet()
            ) {
                JCheckBox checkBox02 = new JCheckBox(uaInfo+"（未授权）");
                checkBox02.setEnabled(false);
                checkBox02.setSelected(false);
                devicePanel.add(checkBox02);
            }
        //}

        f.setContentPane(contentPane);
    }

    public void SetProgress(){
        if(curProgress < MAX_PROGRESS){
            curProgress++;
            progressBar.setValue(curProgress);
        }
        else if(curProgress >= MAX_PROGRESS){
            progressBar.setVisible(false);

        }
    }

    public void SetProgressMaxValue(int maxValue){
        MAX_PROGRESS = maxValue;
        progressBar.setMaximum(MAX_PROGRESS);
    }
/*
    public String GetAPKName(){
        return APKName.getText();
    }
    */
/*
    public String GetWifiName(){
        return wifiName.getText();
    }

    public String GetWifiPass(){
        return wifiPass.getText();
    }
*/
    class OnInstallButtonClick implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //deviceInfo.append(System.getProperty("user.dir") + File.separator + "goldfinger.apk");
            progressBar.setVisible(true);
            HotplugListener.InstallAll();
        }
    }

    class OnRefleshButtonClick implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new DevicesThread()).start();
        }
    }

    class CheckBoxTest implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            System.out.println(checkBox.getText() + " 是否选中: " + checkBox.isSelected());
            if (checkBox.isSelected()){
                HotplugListener.AddInstallList(checkBox.getText());
            }
            else {
                HotplugListener.RemoveInstallList(checkBox.getText());
            }
        }
    }


}
