package com.app.emolist.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To Do List");

        TaskPanel taskPanel = new TaskPanel();
        CalendarPanel calendarPanel = new CalendarPanel(taskPanel);

        HBox root = new HBox(20, taskPanel.getView(), calendarPanel.getView());
        root.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(root, 600, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
