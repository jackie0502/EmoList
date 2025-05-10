package com.app.emolist.GUI;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.time.LocalDate;

public class CalendarPanel {

    private VBox view = new VBox(10);
    private DatePicker calendar = new DatePicker();
    private Label selectedDateLabel = new Label("尚未選擇日期");

    public CalendarPanel(TaskPanel taskPanel) {
        taskPanel.setCalendar(calendar); // 將 calendar 傳給 TaskPanel

        Label title = new Label("📅 萬年曆");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        calendar.setPromptText("選擇日期");

        calendar.setOnAction(e -> {
            LocalDate date = calendar.getValue();
            selectedDateLabel.setText(
                    (date != null) ? "你選擇的日期是：" + date : "尚未選擇日期"
            );
        });

        selectedDateLabel.setStyle("-fx-text-fill: #555;");
        view.getChildren().addAll(title, calendar, selectedDateLabel);
        view.setPadding(new Insets(15));
        view.setPrefWidth(220);
        view.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-background-radius: 8px;");
    }

    public VBox getView() {
        return view;
    }
}
