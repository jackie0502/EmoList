package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

public class CalendarPanelController {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;

    private LocalDate currentMonth;
    private TaskManager taskManager;

    @FXML
    private void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);
//        CalendarPanelController.setTaskPanelController(taskPanelController);
    }

    public TaskPanelController taskPanelController;
    // CalendarPanelController 裡的設定方法
    public void setTaskPanelController(TaskPanelController controller) {
        this.taskPanelController = controller;
    }


    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        updateCalendar();
    }

    public void updateCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")));

        LocalDate firstDayOfMonth = currentMonth;
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1=Monday
        int daysInMonth = firstDayOfMonth.lengthOfMonth();

        // 42格 = 6列7行
        for (int i = 0; i < 42; i++) {
            LocalDate cellDate = firstDayOfMonth.minusDays(firstDayOfWeek - 1).plusDays(i);
            VBox dayBox = createDayBox(cellDate);
            calendarGrid.add(dayBox, i % 7, i / 7);
        }
    }

    private VBox createDayBox(LocalDate date) {
        VBox box = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(5));
        box.setPrefSize(100, 80);
        box.getStyleClass().add("calendar-cell");

        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        if (date.equals(LocalDate.now())) {
            dateLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        long taskCount = taskManager.getAllTasks().stream()
                .filter(task -> date.equals(task.getDeadline()) && !task.isCompleted())  // 只算未完成
                .count();

        Label taskLabel = new Label(taskCount > 0 ? taskCount + " 個未完成任務" : "");
        taskLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        box.getChildren().addAll(dateLabel, taskLabel);

        box.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
                showTasksForDate(date, this::updateCalendar)
        );

        return box;
    }

    public void refreshCalendarView() {
        updateCalendar();  // 重新繪製日曆格子
        System.out.println("Calendar view refreshed.");
    }

    private void showTasksForDate(LocalDate date, Runnable refreshCallback) {
        List<Task> tasksForDate = taskManager.getAllTasks().stream()
                .filter(task -> date.equals(task.getDeadline()) && !task.isCompleted())
                .collect(Collectors.toList());

        Stage taskWindow = new Stage();
        taskWindow.setTitle(date + " 的未完成任務");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getStyleClass().add("container");

        Label headerLabel = new Label("📅 " + date + " 的未完成任務");
        headerLabel.getStyleClass().addAll("h4", "mb-3");
        root.getChildren().add(headerLabel);

        if (tasksForDate.isEmpty()) {
            Label emptyLabel = new Label("📭 當日無未完成任務");
            emptyLabel.getStyleClass().addAll("text-muted", "lead");
            root.getChildren().add(emptyLabel);
        } else {
            // 放任務CheckBox的容器
            VBox taskListBox = new VBox(5);

            // 存放所有checkbox，方便完成時統一處理
            List<CheckBox> checkBoxes = new java.util.ArrayList<>();

            for (Task task : tasksForDate) {
                // 顯示標題加上類別名稱（假設task.getCategory()回傳類別名稱）
                String titleWithCategory = task.getTitle() + " [" + task.getCategory() + "]";
                CheckBox checkBox = new CheckBox(titleWithCategory);
                checkBox.getStyleClass().add("form-check-input");
                checkBoxes.add(checkBox);
                taskListBox.getChildren().add(checkBox);
            }

            // 加入 ScrollPane 使任務列表可滾動，設定最大高度限制
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(taskListBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(200);  // 你可以調整這個高度限制
            root.getChildren().add(scrollPane);

            // 新增一個按鈕區塊
            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox();
            buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));

            javafx.scene.control.Button completeBtn = new javafx.scene.control.Button("完成並關閉");
            completeBtn.setOnAction(e -> {
                // 把勾選的任務標記完成
                for (int i = 0; i < checkBoxes.size(); i++) {
                    CheckBox cb = checkBoxes.get(i);
                    if (cb.isSelected()) {
                        tasksForDate.get(i).setCompleted(true);
                    }
                }

                // 確保同步儲存到 JSON
                taskManager.saveAll();

                // 刷新任務面板與日曆
                if (taskPanelController != null) taskPanelController.refreshTaskViews();
                if (refreshCallback != null) refreshCallback.run();
                taskWindow.close();
            });

            buttonBox.getChildren().add(completeBtn);
            root.getChildren().add(buttonBox);
        }

        Scene scene = new Scene(root, 400, 300);
        taskWindow.setScene(scene);
        taskWindow.initModality(Modality.APPLICATION_MODAL);
        taskWindow.show();
    }






    @FXML
    private void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }
}