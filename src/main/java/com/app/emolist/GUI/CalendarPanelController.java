package com.app.emolist.GUI;

import com.app.emolist.Controller.TaskManager;
import com.app.emolist.GUI.CalendarPanel.CalendarCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarPanelController {
    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;

    private LocalDate currentMonth;
    private TaskManager taskManager;
    public TaskPanelController taskPanelController;

    @FXML
    private void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);
    }

    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        updateCalendar();
    }

    public void setTaskPanelController(TaskPanelController controller) {
        this.taskPanelController = controller;
    }

    public void updateCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")));

        CalendarCellFactory cellFactory = new CalendarCellFactory(taskManager, taskPanelController, this::updateCalendar);

        for (int i = 0; i < 42; i++) {
            LocalDate date = currentMonth.withDayOfMonth(1)
                    .minusDays(currentMonth.withDayOfMonth(1).getDayOfWeek().getValue() - 1)
                    .plusDays(i);
            calendarGrid.add(cellFactory.createDayCell(date), i % 7, i / 7);
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
