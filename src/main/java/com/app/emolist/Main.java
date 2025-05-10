package com.app.emolist;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.LocalDate;

public class Main extends Application {

    // ========== 資料欄位 ==========
    private TaskManager taskManager = new TaskManager();
    private ListView<Task> taskListView = new ListView<>();
    private DatePicker calendar = new DatePicker();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To Do List");
        //褚寶貝好帥

        // ========== 左側輸入與任務按鈕區 ==========
        TextField inputField = new TextField();
        inputField.setPromptText("輸入任務...");
        Button addButton = new Button("新增任務");
        Button completeButton = new Button("標記完成");
        Button deleteButton = new Button("刪除任務");

        // ========== 新增任務按鈕邏輯 ==========
        addButton.setOnAction(e -> {
            String title = inputField.getText().trim();
            LocalDate selectedDate = calendar.getValue();
            if (!title.isEmpty() && selectedDate != null) {
                Task task = new Task(title, selectedDate);
                taskManager.addTask(task);
                taskListView.getItems().add(task);
                inputField.clear();
            } else if (selectedDate == null) {
                showAlert("請先選擇日期！");
            }
        });

        // ========== 標記完成按鈕邏輯 ==========
        completeButton.setOnAction(e -> {
            Task selected = taskListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.toggleCompleted();
                taskListView.refresh();
            } else {
                showAlert("請選擇要標記完成的任務！");
            }
        });

        // ========== 刪除任務按鈕邏輯 ==========
        deleteButton.setOnAction(e -> {
            Task selected = taskListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                taskManager.getTasks().remove(selected);
                taskListView.getItems().remove(selected);
            } else {
                showAlert("請選擇要刪除的任務！");
            }
        });

        // ========== 左側區塊組裝 ==========
        HBox inputBox = new HBox(10, inputField, addButton);
        HBox actionButtons = new HBox(10, completeButton, deleteButton);
        VBox leftPanel = new VBox(10, taskListView, inputBox, actionButtons);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setPrefWidth(350);

        // ========== 右側萬年曆區塊 ==========
        Label calendarLabel = new Label("📅 萬年曆");
        calendarLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        calendar.setPromptText("選擇日期");
        calendar.setStyle("-fx-font-size: 14px;");

        Label selectedDateLabel = new Label("尚未選擇日期");
        selectedDateLabel.setStyle("-fx-text-fill: #555;");

        // ========== 選擇日期時更新標籤 ==========
        calendar.setOnAction(e -> {
            LocalDate selectedDate = calendar.getValue();
            if (selectedDate != null) {
                selectedDateLabel.setText("你選擇的日期是：" + selectedDate.toString());
            } else {
                selectedDateLabel.setText("尚未選擇日期");
            }
        });

        // ========== 右側區塊組裝 ==========
        VBox calendarBox = new VBox(10, calendarLabel, calendar, selectedDateLabel);
        calendarBox.setPadding(new Insets(15));
        calendarBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        calendarBox.setPrefWidth(200);

        VBox rightPanel = new VBox(calendarBox);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(220);

        // ========== 主畫面 ==========
        HBox root = new HBox(20, leftPanel, rightPanel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ========== 顯示警告視窗 ==========
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ========== 主方法 ==========
    public static void main(String[] args) {
        launch(args);
    }
}
