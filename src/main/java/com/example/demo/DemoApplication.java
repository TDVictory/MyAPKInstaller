package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        aw = new AdbWindow();
        System.out.println();
        SpringApplication.run(DemoApplication.class, args);
    }
    public static AdbWindow aw;
    @Override
    public void run(String... args) throws Exception {

        new Thread(new Hotplugger()).start();
        Thread.currentThread().join();
    }
}
