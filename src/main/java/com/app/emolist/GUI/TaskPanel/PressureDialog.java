package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PressureDialog {

    private final List<Task> taskList;
    private final Map<Task, ToggleGroup> inputMap = new HashMap<>();

    public PressureDialog(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Map<Task, Integer> showAndWait() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("設定壓力指數");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        for (Task task : taskList) {
            Label label = new Label(task.getTitle());
            Label stressLabel = new Label("壓力指數:");

            HBox radioBox = new HBox(15);
            ToggleGroup group = new ToggleGroup();

            for (int i = 1; i <= 5; i++) {
                RadioButton rb = new RadioButton(String.valueOf(i));
                rb.setToggleGroup(group);
                radioBox.getChildren().add(rb);
            }

            inputMap.put(task, group);

            VBox taskBox = new VBox(5, label, stressLabel, radioBox);
            taskBox.setPadding(new Insets(5));
            taskBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
            root.getChildren().add(taskBox);
        }

        Button confirm = new Button("確認完成");
        confirm.setOnAction(e -> dialogStage.close());
        root.getChildren().add(confirm);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(550);

        Scene scene = new Scene(scrollPane, 450, 550);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        // 收集輸入值
        Map<Task, Integer> result = new HashMap<>();
        for (Map.Entry<Task, ToggleGroup> entry : inputMap.entrySet()) {
            Toggle selected = entry.getValue().getSelectedToggle();
            if (selected instanceof RadioButton rb) {
                result.put(entry.getKey(), Integer.parseInt(rb.getText()));
            }
        }

        return result;
    }
}