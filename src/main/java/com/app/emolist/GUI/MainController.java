package com.app.emolist.GUI;

import com.app.emolist.Controller.TaskManager;
import javafx.fxml.FXML;

public class MainController {
    @FXML private TaskPanelController taskPanel;
    @FXML private CalendarPanelController calendarPanel;

    private TaskManager taskManager;

    @FXML
    private void initialize() {
        taskManager = new TaskManager();
        taskPanel.setTaskManager(taskManager);
        calendarPanel.setTaskManager(taskManager);
    }
}
