package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.app.emolist.GUI.NotificationHelper;  // 匯入新工具類
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class DeadlineHelper {

    private static TaskPanelController controller = new TaskPanelController();

    public DeadlineHelper(TaskPanelController controller) {
        this.controller = controller;
    }


    public static void checkDeadlines() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0); // 精準到分鐘

        StringBuilder warnings = new StringBuilder();

        for (Task task : controller.getTaskManager().getAllTasks()) {
            if (task.isCompleted() || task.getDeadline() == null || !task.isNotificationEnabled()) {
                continue;
            }

            // 檢查是否到達通知時間
            int daysBefore = task.getNotifyDaysBefore();
            LocalDateTime notifyDateTime = task.getDeadline()
                    .minusDays(daysBefore)
                    .atTime(task.getNotifyTime() != null ? task.getNotifyTime() : LocalTime.of(9, 0)); // 預設 9:00

            if (now.equals(notifyDateTime)) {
                warnings.append("【提醒】").append(task.getTitle())
                        .append("（截止日：").append(task.getDeadline()).append("）\n");
            }
        }

        if (!warnings.isEmpty()) {
            TextArea textArea = new TextArea(warnings.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefWidth(400);
            textArea.setPrefHeight(300);
            textArea.setMaxHeight(Region.USE_PREF_SIZE);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("任務通知提醒");
            dialog.setHeaderText("以下任務到達通知時間：");
            dialog.getDialogPane().setContent(textArea);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.show();

            NotificationHelper.showSystemNotification("任務提醒", warnings.toString());
        }
    }



}
