package com.app.emolist.GUI;

import com.app.emolist.GUI.TaskPanelController;
import javafx.application.Platform;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MidnightScheduler {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private TaskPanelController taskController;

    public MidnightScheduler(TaskPanelController taskController) {
        this.taskController = taskController;
    }

    public void start() {
        long initialDelay = calculateInitialDelay();
        long period = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (taskController != null) {
                    taskController.checkDeadlines();
                }
            });
        }, initialDelay, period, TimeUnit.SECONDS);
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime targetTime = LocalTime.of(0, 0); // 半夜 00:00
        LocalDateTime nextRun = now.toLocalDate().atTime(targetTime);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).getSeconds();
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
