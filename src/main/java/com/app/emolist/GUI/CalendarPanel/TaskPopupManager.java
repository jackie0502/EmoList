package com.app.emolist.GUI.CalendarPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import com.app.emolist.GUI.TaskPanel.PressureDialog;
import com.app.emolist.GUI.TaskPanelController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskPopupManager {
    private final TaskManager taskManager;
    private final TaskPanelController taskPanelController;

    public TaskPopupManager(TaskManager manager, TaskPanelController controller) {
        this.taskManager = manager;
        this.taskPanelController = controller;
    }

    public void showTasksForDate(LocalDate date, Runnable refreshCallback) {
        List<Task> tasks = taskManager.getAllTasks().stream()
                .filter(task -> date.equals(task.getDeadline()) && !task.isCompleted())
                .collect(Collectors.toList());

        Stage stage = new Stage();
        stage.setTitle(date + " 的未完成任務");
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getStyleClass().add("container");

        Label header = new Label("📅 " + date + " 的未完成任務");
        header.getStyleClass().addAll("h4", "mb-3");
        root.getChildren().add(header);

        if (tasks.isEmpty()) {
            root.getChildren().add(new Label("📭 當日無未完成任務"));
        } else {
            List<CheckBox> checkBoxes = new ArrayList<>();
            VBox taskListBox = new VBox(5);

            for (Task task : tasks) {
                CheckBox cb = new CheckBox(task.getTitle() + " [" + task.getCategory() + "]");
                cb.getStyleClass().add("form-check-input");
                taskListBox.getChildren().add(cb);
                checkBoxes.add(cb);
            }

            ScrollPane scrollPane = new ScrollPane(taskListBox);
            scrollPane.setPrefHeight(200);
            scrollPane.setFitToWidth(true);
            root.getChildren().add(scrollPane);

            Button completeBtn = new Button("完成並關閉");
            completeBtn.setOnAction(e -> {
                // ✅ 根據勾選框找出被選中的任務
                List<Task> selectedTasks = new ArrayList<>();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        selectedTasks.add(tasks.get(i));
                    }
                }

                // ✅ 篩選出尚未完成的任務
                List<Task> incompleteTasks = selectedTasks.stream()
                        .filter(task -> !task.isCompleted())
                        .collect(Collectors.toList());

                if (incompleteTasks.isEmpty()) return;

                // ✅ 顯示壓力指數輸入對話框
                PressureDialog dialog = new PressureDialog(new ArrayList<>(incompleteTasks));
                Map<Task, Integer> stressMap = dialog.showAndWait();
                if (stressMap == null) return;

                for (Task task : incompleteTasks) {
                    taskManager.setTaskCompleted(task, true); // 標記為完成

                    // ✅ 記錄壓力指數
                    if (stressMap.containsKey(task)) {
                        task.setStressLevel(stressMap.get(task));
                    }

                    // 🔁 處理 recurring 任務
                    if (!"無".equals(task.getRecurrence()) && task.getDeadline() != null) {
                        LocalDate nextDeadline = null;
                        switch (task.getRecurrence()) {
                            case "每天":
                                nextDeadline = task.getDeadline().plusDays(1);
                                break;
                            case "每週":
                                nextDeadline = task.getDeadline().plusWeeks(1);
                                break;
                            case "每月":
                                nextDeadline = task.getDeadline().plusMonths(1);
                                break;
                        }

                        if (nextDeadline != null) {
                            Task newTask = new Task(
                                    task.getTitle(),
                                    nextDeadline,
                                    task.getCategory(),
                                    task.getPriority(),
                                    task.getTags(),
                                    task.getRecurrence()
                            );
                            taskManager.addTask(newTask);
                        }
                    }
                }

                // ✅ 更新畫面與儲存
                taskPanelController.updatePanels();
                taskPanelController.refreshTaskViews();
                TaskRepository.saveTasks(taskManager.getAllTasks());


                stage.close();
                if (refreshCallback != null) refreshCallback.run();
            });


            HBox buttonBox = new HBox(completeBtn);
            buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            root.getChildren().add(buttonBox);
        }

        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }
}
