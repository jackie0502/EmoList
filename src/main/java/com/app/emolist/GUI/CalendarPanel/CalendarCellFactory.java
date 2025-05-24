package com.app.emolist.GUI.CalendarPanel;

import com.app.emolist.Controller.TaskManager;
import com.app.emolist.GUI.TaskPanelController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class CalendarCellFactory {

    private final TaskManager taskManager;
    private final TaskPanelController taskPanelController;
    private final Runnable refreshCallback;

    public CalendarCellFactory(TaskManager taskManager, TaskPanelController controller, Runnable refreshCallback) {
        this.taskManager = taskManager;
        this.taskPanelController = controller;
        this.refreshCallback = refreshCallback;
//        taskPanelController.refreshTaskViews(); // 同步顯示任務

    }

    public VBox createDayCell(LocalDate date) {
        VBox box = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(5));
        box.setPrefSize(100, 80);
        box.getStyleClass().add("calendar-cell");

        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.getStyleClass().add("date-label");
        if (date.equals(LocalDate.now())) {
            dateLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        long taskCount = taskManager.getAllTasks().stream()
                .filter(task -> date.equals(task.getDeadline()) && !task.isCompleted())
                .count();

        Label taskLabel = new Label(taskCount > 0 ? taskCount + " 個未完成任務" : "");
        taskLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        box.getChildren().addAll(dateLabel, taskLabel);

        box.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
                new TaskPopupManager(taskManager, taskPanelController)
                        .showTasksForDate(date, refreshCallback)
        );

        return box;
    }
}
