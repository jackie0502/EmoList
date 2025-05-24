package com.app.emolist.GUI;

import com.app.emolist.Controller.TaskManager;
import com.app.emolist.GUI.CalendarPanel.CalendarCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarPanelController {
    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;

    private LocalDate currentMonth;
    private TaskManager taskManager;
    public TaskPanelController taskPanelController;

    private final String[] dayNames = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    @FXML
    private void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);

        // 加入星期列標題（放在 GridPane 最上方）
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            calendarGrid.add(dayLabel, i, 0);
        }
    }

    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        updateCalendar();
    }

    public void setTaskPanelController(TaskPanelController controller) {
        this.taskPanelController = controller;
    }

    public void updateCalendar() {
        // 清除除星期列外的子元件（星期列在第0列）
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")));

        CalendarCellFactory cellFactory = new CalendarCellFactory(taskManager, taskPanelController, this::updateCalendar);

        // 本月第一天的星期幾（1=星期一, 7=星期日）
        int firstDayOfWeekValue = currentMonth.getDayOfWeek().getValue();

        // Java 的 DayOfWeek 星期日是7，所以要特別處理，讓星期日是0欄，星期一是1欄...
        int startOffset = (firstDayOfWeekValue == 7) ? 0 : firstDayOfWeekValue;

        LocalDate firstCalendarDate = currentMonth.minusDays(startOffset);


        for (int i = 0; i < 42; i++) {
            LocalDate date = firstCalendarDate.plusDays(i);
            VBox dayCell = cellFactory.createDayCell(date);

            // 非當月日期標灰色
            if (!date.getMonth().equals(currentMonth.getMonth())) {
                if (!dayCell.getStyleClass().contains("outside-month")) {
                    dayCell.getStyleClass().add("outside-month");
                }

                // 修改這段：讓裡面的 Label（日期字）加上 "outside-month"
                for (var node : dayCell.getChildren()) {
                    if (node instanceof Label label && label.getStyleClass().contains("date-label")) {
                        if (!label.getStyleClass().contains("outside-month")) {
                            label.getStyleClass().add("outside-month");
                        }
                    }
                }
            } else {
                dayCell.getStyleClass().remove("outside-month");

                for (var node : dayCell.getChildren()) {
                    if (node instanceof Label label && label.getStyleClass().contains("date-label")) {
                        label.getStyleClass().remove("outside-month");
                    }
                }
            }



            // 因為星期列是第0列，日期從第1列開始，row = i/7 + 1
            calendarGrid.add(dayCell, i % 7, i / 7 + 1);
        }

        if (taskPanelController != null) {
            taskPanelController.refreshTaskViews();
        }
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

    public void refreshCalendarView() {
        updateCalendar();
    }
}
