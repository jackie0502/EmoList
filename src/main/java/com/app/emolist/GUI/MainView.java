package com.app.emolist.GUI;

import com.app.emolist.Controller.TaskManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class MainView extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 建立共享 TaskManager
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
            taskController.setCalendarController(calendarController);
            calendarController.setTaskPanelController(taskController);
            taskController.setStatsController(statsController);

            // 啟動 MidnightScheduler，傳入 taskController
            MidnightScheduler scheduler = new MidnightScheduler(taskController);
            scheduler.start();

            // 主畫面排版
            VBox rightVBox = new VBox(10);
            rightVBox.setPadding(new Insets(0));

// 👉 用 HBox 包住 calendarPanel，並置中
            HBox calendarBox = new HBox(calendarPanel);
            calendarBox.setAlignment(Pos.CENTER);
            VBox.setVgrow(calendarBox, Priority.ALWAYS);
            // 主畫面 HBox（左 Task、右 Calendar+Stats）
            // 主畫面 HBox（左 Task、右 Calendar+Stats）
            HBox mainLayout = new HBox(10);
            mainLayout.setPadding(new Insets(10));
            mainLayout.getChildren().addAll(taskPanel, rightVBox);



// statsPanel 固定高度
            VBox.setVgrow(statsPanel, Priority.NEVER);

// 👉 放入 VBox
            rightVBox.getChildren().addAll(calendarBox, statsPanel);

// 綁定左右固定 30% / 70%
            mainLayout.widthProperty().addListener((obs, oldVal, newVal) -> {
                double width = newVal.doubleValue();
                taskPanel.prefWidth(width * 0.4);
                rightVBox.setPrefWidth(width * 0.6);
            });

            Scene scene = new Scene(mainLayout, 1200, 800);

            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/com/app/emolist/GUI/view/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();


            primaryStage.setTitle("EmoList - To Do List");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(600);
            primaryStage.show();

            // 啟動時先檢查 Deadline
            taskController.checkDeadlines();

            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
