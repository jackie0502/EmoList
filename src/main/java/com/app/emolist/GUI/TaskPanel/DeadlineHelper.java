package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import com.app.emolist.GUI.NotificationHelper;  // 匯入新工具類

public class DeadlineHelper {

    private static TaskPanelController controller = new TaskPanelController();

    public DeadlineHelper(TaskPanelController controller) {
        this.controller = controller;
    }


    public static void checkDeadlines() {
        LocalDate today = LocalDate.now();
        StringBuilder warnings = new StringBuilder();

        for (Task task : controller.getTaskManager().getAllTasks()) {
            if (!task.isCompleted() && task.getDeadline() != null) {
                if (task.getDeadline().isBefore(today)) {
                    warnings.append("【過期】").append(task.getTitle()).append("（").append(task.getDeadline()).append("）\n");
                } else if (task.getDeadline().equals(today)) {
                    warnings.append("【今天截止】").append(task.getTitle()).append("（").append(task.getDeadline()).append("）\n");
                }
                else if(task.getDeadline().isBefore(LocalDate.now().plusDays(2))){
                    warnings.append("【即將截止】").append(task.getTitle()).append("（").append(task.getDeadline()).append("）\n");
                }
            }
        }

        if (!warnings.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("任務到期提醒");
            alert.setHeaderText("以下任務已到期或即將到期：");
            alert.setContentText(warnings.toString());
            alert.showAndWait();
            NotificationHelper.showSystemNotification("任務提醒", warnings.toString());
        }

    }
}
