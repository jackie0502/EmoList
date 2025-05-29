package com.app.emolist.GUI;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationHelper {

    public static void showSystemNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            System.out.println("系統不支援通知");
            return;
        }

        new Thread(() -> {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage(""); // 可指定 icon 圖片或保持為空
                TrayIcon trayIcon = new TrayIcon(image, "Emolist 通知");
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("Emolist 任務提醒");
                tray.add(trayIcon);

                trayIcon.displayMessage(title, message, MessageType.INFO);

                // 背景線程等待幾秒後移除 tray icon
                Thread.sleep(3000);
                tray.remove(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); // 啟動背景執行緒
    }
}
