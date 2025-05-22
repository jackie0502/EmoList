package com.app.emolist.GUI;

import com.app.emolist.Controller.TaskManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 建立共享的 TaskManager
            TaskManager sharedTaskManager = new TaskManager();

            // 載入 TaskPanel.fxml 並取得 Controller
            FXMLLoader taskLoader = new FXMLLoader(getClass().getResource("/com/app/emolist/GUI/view/TaskPanel.fxml"));
            Parent taskPanel = taskLoader.load();
            TaskPanelController taskController = taskLoader.getController();
            taskController.setTaskManager(sharedTaskManager);

            // 載入 CalendarPanel.fxml 並取得 Controller
            FXMLLoader calendarLoader = new FXMLLoader(getClass().getResource("/com/app/emolist/GUI/view/CalendarPanel.fxml"));
            Parent calendarPanel = calendarLoader.load();
            CalendarPanelController calendarController = calendarLoader.getController();
            calendarController.setTaskManager(sharedTaskManager);

            // 載入 StatsPanel.fxml 並取得 Controller
            FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("/com/app/emolist/GUI/view/StatsPanel.fxml"));
            Parent statsPanel = statsLoader.load();
            StatsPanelController statsController = statsLoader.getController();
            statsController.setTaskManager(sharedTaskManager);

            // 建立彼此注入
//            taskController.setCalendarController(calendarController);
//            taskController.setStatsController(statsController);
            // 建立彼此注入
            taskController.setCalendarController(calendarController);
            calendarController.setTaskPanelController(taskController);
            taskController.setStatsController(statsController);


            // 主畫面排版
            HBox topRow = new HBox(20, taskPanel, calendarPanel);
            VBox root = new VBox(10, topRow, statsPanel);
            root.setPadding(new javafx.geometry.Insets(10));

            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/com/app/emolist/GUI/view/style.css").toExternalForm());

            primaryStage.setTitle("EmoList - To Do List");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(600);
            primaryStage.show();

            // 啟動時檢查 Deadline
            taskController.checkDeadlines();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
