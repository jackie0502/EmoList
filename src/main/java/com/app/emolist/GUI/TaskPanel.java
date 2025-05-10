package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.time.LocalDate;

public class TaskPanel {

    private TaskManager taskManager = new TaskManager();
    private ListView<Task> taskListView = new ListView<>();
    private TextField inputField = new TextField();
    private DatePicker calendar;  // 由 CalendarPanel 傳入

    private VBox view;

    public TaskPanel() {
        view = createView();
    }

    public void setCalendar(DatePicker calendar) {
        this.calendar = calendar;
    }

    public VBox getView() {
        return view;
    }

    private VBox createView() {
        inputField.setPromptText("輸入任務...");
        Button addButton = new Button("新增任務");
        Button completeButton = new Button("標記完成");
        Button deleteButton = new Button("刪除任務");

        addButton.setOnAction(e -> handleAddTask());
        completeButton.setOnAction(e -> handleCompleteTask());
        deleteButton.setOnAction(e -> handleDeleteTask());

        HBox inputBox = new HBox(10, inputField, addButton);
        HBox actionButtons = new HBox(10, completeButton, deleteButton);

        VBox panel = new VBox(10, taskListView, inputBox, actionButtons);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(350);
        return panel;
    }

    // ========== 輸入任務並選擇日期 ==========
    private void handleAddTask() {
        String title = inputField.getText().trim();
        LocalDate selectedDate = (calendar != null) ? calendar.getValue() : null;
        if (!title.isEmpty() && selectedDate != null) {
            Task task = new Task(title, selectedDate);
            taskManager.addTask(task);
            taskListView.getItems().add(task);
            inputField.clear();
        } else {
            showAlert("請先輸入任務與選擇日期！");
        }
    }

    // ========== 標記完成 ==========
    private void handleCompleteTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.toggleCompleted();
            taskListView.refresh();
        } else {
            showAlert("請選擇要標記完成的任務！");
        }
    }

    // ========== 刪除 ==========
    private void handleDeleteTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskManager.getTasks().remove(selected);
            taskListView.getItems().remove(selected);
        } else {
            showAlert("請選擇要刪除的任務！");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
