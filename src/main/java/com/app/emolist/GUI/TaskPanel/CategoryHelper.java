package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.GUI.TaskPanelController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class CategoryHelper {

    private final TaskPanelController controller;

    public CategoryHelper(TaskPanelController controller) {
        this.controller = controller;
    }

    public void refreshCategoryTabs() {
        Platform.runLater(this::actuallyRefreshCategoryTabs);
    }

    private void actuallyRefreshCategoryTabs() {
        HBox tabs = controller.getCategoryTabs();
        ComboBox<String> dropdown = controller.getCategoryDropdown();
        tabs.getChildren().clear();
        dropdown.getItems().clear();
        dropdown.setVisible(false);

        List<String> allCategories = controller.getTaskCategoryChoice().getItems();

        // ✅ 加入「全部」按鈕
        Button allButton = new Button("全部");
        allButton.setOnAction(e -> {
            controller.setCurrentCategoryFilter("全部");
            controller.refreshTaskViews();
            highlightTab("全部");
        });
        tabs.getChildren().add(allButton);

        // ✅ 控制分類顯示數量（第 1 ～ 2 項）
        int maxInline = 2;
        List<String> overflowCategories = new ArrayList<>();

        int index = 0;
        for (String cat : allCategories) {
            if (cat.equals("無")) continue;

            if (index < maxInline) {
                tabs.getChildren().add(createTabButton(cat));
            } else {
                overflowCategories.add(cat);
            }
            index++;
        }

        // ✅ 加入下拉式選單
        if (!overflowCategories.isEmpty()) {
            dropdown.getItems().setAll(overflowCategories);
            dropdown.setVisible(true);

            dropdown.setOnAction(e -> {
                String selected = dropdown.getValue();
                if (selected != null) {
                    controller.setCurrentCategoryFilter(selected);
                    controller.refreshTaskViews();
                    highlightTab(null);
                    Platform.runLater(() -> dropdown.setValue(null));
                }
            });
        }

        highlightTab(controller.getCurrentCategoryFilter());
    }




    private Button createTabButton(String category) {
        Button button = new Button(category);
        button.setOnAction(e -> {
            controller.setCurrentCategoryFilter(category);
            controller.refreshTaskViews();
            highlightTab(category);
        });
        return button;
    }

    private void highlightTab(String selected) {
        for (Node node : controller.getCategoryTabs().getChildren()) {
            if (node instanceof Button btn) {
                if (btn.getText().equals(selected)) {
                    btn.setStyle("-fx-background-color: #90caf9;");
                } else {
                    btn.setStyle("");
                }
            }
        }
    }
}
