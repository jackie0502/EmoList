package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.time.LocalDate;
import java.util.List;

public class CalendarPanelController {
    @FXML private VBox view;
    @FXML private Label titleLabel;
    @FXML private DatePicker calendar;
    @FXML private Label selectedDateLabel;

    private TaskManager taskManager;

    @FXML
    private void initialize() {
        selectedDateLabel.setStyle("-fx-text-fill: #555;");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // 使用 DatePickerSkin 讓日曆常駐
        DatePickerSkin skin = new DatePickerSkin(calendar);
        Node calendarContent = skin.getPopupContent();

        view.getChildren().add(1, calendarContent); // 插入在 titleLabel 下方
        configureDayCells();

        calendar.setOnAction(e -> {
            LocalDate date = calendar.getValue();
            if (date != null) {
                showTasksForDate(date);
            } else {
                selectedDateLabel.setText("尚未選擇日期");
            }
        });
    }

    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        configureDayCells();
    }

    public void refreshCalendarView() {
        configureDayCells();
    }

    private void configureDayCells() {
        calendar.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null || taskManager == null) {
                    setText(null);
                    setStyle("");
                    setTooltip(null);
                } else {
                    int count = 0;
                    int pressure = 0;
                    for (Task task : taskManager.getTasks()) {
                        if (date.equals(task.getDeadline())) {
                            count++;
                            pressure += task.getPriority();
                        }
                    }

                    setText(date.getDayOfMonth() + (count > 0 ? " (" + count + ")" : ""));
                    if (count > 0) {
                        Tooltip tooltip = new Tooltip("任務: " + count + " 個\n壓力指數: " + pressure);
                        setTooltip(tooltip);

                        if (pressure <= 3)
                            setStyle("-fx-background-color: #c8e6c9;");
                        else if (pressure <= 6)
                            setStyle("-fx-background-color: #fff9c4;");
                        else
                            setStyle("-fx-background-color: #ffcdd2;");
                    } else {
                        setStyle("");
                        setTooltip(null);
                    }
                }
            }
        });
    }

    private void showTasksForDate(LocalDate date) {
        if (taskManager == null) return;
        int count = 0;
        int pressure = 0;
        for (Task task : taskManager.getTasks()) {
            if (date.equals(task.getDeadline())) {
                count++;
                pressure += task.getPriority();
            }
        }

        if (count > 0) {
            selectedDateLabel.setText("你選擇的日期是：" + date + "（" + count + " 個任務，壓力指數 " + pressure + "）");
        } else {
            selectedDateLabel.setText("你選擇的日期是：" + date + "（無任務）");
        }
    }
}
