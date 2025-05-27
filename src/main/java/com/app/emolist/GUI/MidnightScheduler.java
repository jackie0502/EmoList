package com.app.emolist.GUI;

import com.app.emolist.GUI.TaskPanel.DeadlineHelper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.app.emolist.GUI.TaskPanel.DeadlineHelper;
public class MidnightScheduler {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();



    public void start() {
        long initialDelay = calculateInitialDelay();
        long period = TimeUnit.DAYS.toSeconds(1); // 每 24 小時執行一次

        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                // 在這裡執行更新任務
                showNotification("EmoList", "已於晚上 00:00 自動更新！");
                DeadlineHelper.checkDeadlines();
            });
        }, initialDelay, period, TimeUnit.SECONDS);
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime targetTime = LocalTime.of(00, 00);
        LocalDateTime nextRun = now.toLocalDate().atTime(targetTime);

        if (now.isAfter(nextRun)) {
            // 如果現在已經超過今天的 20:36，則安排明天的
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).getSeconds();
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
