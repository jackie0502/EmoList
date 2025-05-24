package com.app.emolist.GUI.CalendarPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.util.List;

public class TaskCompletionHandler {
    private final TaskManager taskManager;
    private final TaskPanelController taskPanelController;

    public TaskCompletionHandler(TaskManager taskManager, TaskPanelController taskPanelController) {
        this.taskManager = taskManager;
        this.taskPanelController = taskPanelController;
    }

    public void completeTasks(List<Task> tasks, List<CheckBox> checkBoxes, Runnable refreshCallback, Stage stage) {
        for (int i = 0; i < tasks.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                tasks.get(i).setCompleted(true);
            }
        }

        taskManager.saveAll();
//        taskManager.loadAll();

        if (taskPanelController != null) taskPanelController.refreshTaskViews();
        if (refreshCallback != null) refreshCallback.run();
        stage.close();
    }
}
