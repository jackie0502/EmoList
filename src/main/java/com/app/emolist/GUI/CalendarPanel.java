package com.app.emolist.GUI;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.util.List;
import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanel;

public class CalendarPanel {
    private VBox view = new VBox(10);
    private DatePicker calendar = new DatePicker();
    private Label titleLabel;
    private Label selectedDateLabel = new Label("尚未選擇日期");
    private TaskPanel taskPanel;

    public CalendarPanel(TaskPanel taskPanel) {
        this.taskPanel = taskPanel;
        taskPanel.setCalendar(calendar);
        titleLabel = new Label("📅 萬年曆");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        calendar.setPromptText("選擇日期");

        // 配置日期單元格工廠以顯示每日任務數及壓力
        configureDayCells();

        calendar.setOnAction(e -> {
            LocalDate date = calendar.getValue();
            if (date != null) {
                // 計算所選日期的任務數量和壓力指數
                List<Task> tasks = taskPanel.getTaskManager().getTasks();
                int count = 0;
                int pressure = 0;
                for (Task task : tasks) {
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
            } else {
                selectedDateLabel.setText("尚未選擇日期");
            }
        });

        selectedDateLabel.setStyle("-fx-text-fill: #555;");
        view.getChildren().addAll(titleLabel, calendar, selectedDateLabel);
        view.setPadding(new Insets(15));
        view.setPrefWidth(220);
        view.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-background-radius: 8px;");
    }

    private void configureDayCells() {
        calendar.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                    setStyle("");
                    setTooltip(null);
                } else {
                    // 預設文字為該日日期號
                    setText(String.valueOf(date.getDayOfMonth()));
                    // 統計當日任務數與重要性總和（壓力）
                    List<Task> tasks = taskPanel.getTaskManager().getTasks();
                    int count = 0;
                    int pressure = 0;
                    for (Task task : tasks) {
                        if (date.equals(task.getDeadline())) {
                            count++;
                            pressure += task.getPriority();
                        }
                    }
                    if (count > 0) {
                        // 顯示任務數於日期後
                        setText(date.getDayOfMonth() + " (" + count + ")");
                        // Tooltip 顯示詳情
                        Tooltip tip = new Tooltip("任務: " + count + " 個\n壓力指數: " + pressure);
                        setTooltip(tip);
                        // 根據壓力指數設定底色
                        if (pressure <= 3) {
                            setStyle("-fx-background-color: #c8e6c9;"); // 淺綠
                        } else if (pressure <= 6) {
                            setStyle("-fx-background-color: #fff9c4;"); // 淺黃
                        } else {
                            setStyle("-fx-background-color: #ffcdd2;"); // 淺紅
                        }
                    } else {
                        setStyle("");
                        setTooltip(null);
                    }
                }
            }
        });
    }

    public VBox getView() {
        return view;
    }

    // 刷新行事曆顯示（重新套用日期單元格工廠）
    public void refreshCalendarView() {
        configureDayCells();
    }

    // 主題切換：深色/淺色模式
    public void applyTheme(boolean dark) {
        if (dark) {
            view.setStyle("-fx-background-color: #333333; -fx-border-color: #555555; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
            selectedDateLabel.setStyle("-fx-text-fill: #cccccc;");
            calendar.setStyle("-fx-background-color: #555555; -fx-text-fill: #ffffff;");
        } else {
            view.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-background-radius: 8px;");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000000;");
            selectedDateLabel.setStyle("-fx-text-fill: #555555;");
            calendar.setStyle("");
        }
    }
}
