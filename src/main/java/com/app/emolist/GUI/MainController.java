package com.app.emolist.GUI;

import com.app.emolist.Controller.TaskManager;
import javafx.fxml.FXML;

public class MainController {
    @FXML private TaskPanelController taskPanel;
    @FXML private CalendarPanelController calendarPanel;
    @FXML private StatsPanelController statsPanel; // ✅ 新增這行

    private TaskManager taskManager;

    @FXML
    private void initialize() {
        taskManager = new TaskManager();

        // 設定 TaskManager
        taskPanel.setTaskManager(taskManager);
        calendarPanel.setTaskManager(taskManager);
        statsPanel.setTaskManager(taskManager); // ✅ 加這行

        // 設定控制器彼此的關係
        calendarPanel.setTaskPanelController(taskPanel);
        taskPanel.setStatsController(statsPanel); // ✅ 加這行
    }
}
