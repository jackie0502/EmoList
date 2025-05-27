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
    private final Map<Task, ComboBox<Integer>> inputMap = new HashMap<>();

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
            ComboBox<Integer> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(1, 2, 3, 4, 5);
            comboBox.setPromptText("選擇壓力指數");

            inputMap.put(task, comboBox);

            VBox taskBox = new VBox(5, label, comboBox);
            taskBox.setPadding(new Insets(5));
            taskBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
            root.getChildren().add(taskBox);
        }

        Button confirm = new Button("確認完成");
        confirm.setOnAction(e -> dialogStage.close());

        root.getChildren().add(confirm);

        Scene scene = new Scene(root, 400, 100 + taskList.size() * 80);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        // 收集輸入值
        Map<Task, Integer> result = new HashMap<>();
        for (Map.Entry<Task, ComboBox<Integer>> entry : inputMap.entrySet()) {
            Integer value = entry.getValue().getValue();
            if (value != null) {
                result.put(entry.getKey(), value);
            }
        }

        return result;
    }
}
