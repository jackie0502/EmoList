package com.app.emolist.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.app.emolist.GUI.TaskPanel;
import com.app.emolist.GUI.CalendarPanel;
import com.app.emolist.GUI.StatsPanel;

public class MainView extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To Do List");
        // 初始化各面板
        TaskPanel taskPanel = new TaskPanel();
        CalendarPanel calendarPanel = new CalendarPanel(taskPanel);
        taskPanel.setCalendarPanel(calendarPanel);
        StatsPanel statsPanel = new StatsPanel(taskPanel.getTaskManager());
        taskPanel.setStatsPanel(statsPanel);

        // 版面佈局：上方橫列（任務清單 + 行事曆），下方統計圖表
        HBox topRow = new HBox(20, taskPanel.getView(), calendarPanel.getView());
        VBox root = new VBox(10, topRow, statsPanel.getView());
        root.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 啟動後檢查是否有任務到期需要提醒
        Platform.runLater(() -> taskPanel.checkDeadlines());
    }
}
