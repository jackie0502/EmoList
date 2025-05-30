package com.app.emolist.GUI;

import com.app.emolist.GUI.TaskPanelController;
import javafx.application.Platform;

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
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (taskController != null) {
                    taskController.checkDeadlines();  // 內部會做「時間點判斷」
                }
            });
        }, 0, 1, TimeUnit.MINUTES);  // 每分鐘跑一次
    }


    public void stop() {
        scheduler.shutdownNow();
    }
}
