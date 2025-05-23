package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.Alert;

import java.time.LocalDate;

public class DeadlineHelper {

    private final TaskPanelController controller;

    public DeadlineHelper(TaskPanelController controller) {
        this.controller = controller;
    }

    public void checkDeadlines() {
        LocalDate today = LocalDate.now();
        StringBuilder warnings = new StringBuilder();

        for (Task task : controller.getTaskManager().getAllTasks()) {
            if (!task.isCompleted() && task.getDeadline() != null) {
                if (task.getDeadline().isBefore(today)) {
                    warnings.append("【過期】").append(task.getTitle()).append("（").append(task.getDeadline()).append("）\n");
                } else if (task.getDeadline().equals(today)) {
                    warnings.append("【今天截止】").append(task.getTitle()).append("（").append(task.getDeadline()).append("）\n");
                }
            }
        }

        if (!warnings.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("任務到期提醒");
            alert.setHeaderText("以下任務已到期或即將到期：");
            alert.setContentText(warnings.toString());
            alert.showAndWait();
        }
    }
}
