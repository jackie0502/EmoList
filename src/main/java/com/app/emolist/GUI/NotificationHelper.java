package com.app.emolist.GUI;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationHelper {

    public static void showSystemNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            System.out.println("系統不支援通知");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(""); // 可設為 null 或指定 icon 圖片
            TrayIcon trayIcon = new TrayIcon(image, "Emolist 通知");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Emolist 任務提醒");
            tray.add(trayIcon);

            trayIcon.displayMessage(title, message, MessageType.INFO);

            // 自動移除圖示（可選）
            Thread.sleep(3000);
            tray.remove(trayIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
