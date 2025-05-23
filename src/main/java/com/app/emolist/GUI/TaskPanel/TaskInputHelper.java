package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;

import java.time.LocalDate;

public class TaskInputHelper {

    private final TaskPanelController controller;

    public TaskInputHelper(TaskPanelController controller) {
        this.controller = controller;
    }

    public void showTaskInputBox() {
        controller.getTaskInputBox().setVisible(true);
        controller.getTaskInputBox().setManaged(true);
        controller.getInputField().clear();
        controller.getTaskCategoryChoice().getSelectionModel().select("無");
        controller.getPriorityChoice().getSelectionModel().select("中");
        controller.getRecurrenceChoice().getSelectionModel().select("無");
    }

    public void hideTaskInputBox() {
        controller.getTaskInputBox().setVisible(false);
        controller.getTaskInputBox().setManaged(false);
    }

    public void handleAddTask() {
        String title = controller.getInputField().getText().trim();
        if (title.isEmpty()) return;

        Task task;

//        LocalDate deadline = controller.getTaskDeadlinePicker().getValue(); // 讀取日期
        // 判斷是否有選日期（假設 controller 有一個 DatePicker 名為 taskDeadlinePicker）

//        if (deadline != null) {
//            task = new Task(title, deadline);
//        } else {
//            task = new Task(title); // 沒有選日期，就用 LocalDate.MAX 當作預設
//        }
//        LocalDate deadline =  controller.getDeadline()
        task = new Task(title);
//        Task task = new Task(title, deadline);
//        task.setCreatedDate(LocalDate.now());

        String category = controller.getTaskCategoryChoice().getValue();
        task.setCategory(category != null ? category : "無");
        task.setTags(category);

        String priority = controller.getPriorityChoice().getValue();
        task.setPriority(switch (priority) {
            case "高" -> 3;
            case "中" -> 2;
            default -> 1;
        });

        String recurrence = controller.getRecurrenceChoice().getValue();
        task.setRecurrence(recurrence != null ? recurrence : "無");

        controller.getTaskManager().addTask(task);
        hideTaskInputBox();
        controller.refreshTaskViews();
        controller.updatePanels();
    }
}
