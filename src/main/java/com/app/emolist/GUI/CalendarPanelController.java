package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.layout.Region;


public class CalendarPanelController {
    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;

    private LocalDate currentMonth;
    private TaskManager taskManager;

    @FXML
    private void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);
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

        long taskCount = taskManager.getTasks().stream()
                .filter(task -> date.equals(task.getDeadline()))
                .count();

        Label taskLabel = new Label(taskCount > 0 ? taskCount + " 個任務" : "");
        taskLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        box.getChildren().addAll(dateLabel, taskLabel);

        box.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showTasksForDate(date));

        return box;
    }
    public void refreshCalendarView() {
        // 在這裡加上重新載入行事曆 UI 的邏輯
        System.out.println("Calendar view refreshed.");
    }

    private void showTasksForDate(LocalDate date) {
        List<Task> tasksForDate = taskManager.getTasks().stream()
                .filter(task -> date.equals(task.getDeadline()) && !task.isCompleted())
                .collect(Collectors.toList());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("任務清單");
        alert.setHeaderText(date.toString() + " 的未完成任務");
        String content = tasksForDate.isEmpty()
                ? "當日無未完成任務"
                : "未完成任務 : \n" + tasksForDate.stream().map(Task::getTitle).collect(Collectors.joining("\n"));
        alert.setContentText(content);
        alert.showAndWait();
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
