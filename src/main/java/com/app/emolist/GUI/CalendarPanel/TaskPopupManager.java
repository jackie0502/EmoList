package com.app.emolist.GUI.CalendarPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
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
                new TaskCompletionHandler(taskManager, taskPanelController)
                        .completeTasks(tasks, checkBoxes, refreshCallback, stage);
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
