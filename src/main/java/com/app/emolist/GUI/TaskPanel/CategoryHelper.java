package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Objects;

public class CategoryHelper {

    private final TaskPanelController controller;

    public CategoryHelper(TaskPanelController controller) {
        this.controller = controller;
    }

    public void refreshCategoryTabs() {
        HBox tabs = controller.getCategoryTabs();
        tabs.getChildren().clear();
        controller.getCategoryDropdown().getItems().clear();

        List<String> all = controller.getTaskCategoryChoice().getItems();

        // 全部按鈕
        Button allButton = new Button("全部");
        allButton.setOnAction(e -> {
            controller.setCurrentCategoryFilter("全部");
            controller.refreshTaskViews();
            highlightTab("全部");
        });
        tabs.getChildren().add(allButton);

        for (String cat : all) {
            if (Objects.equals(cat, "無")) continue; // "無" 不顯示在 tabs
            Button tab = createTabButton(cat);
            tabs.getChildren().add(tab);
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

        ContextMenu menu = new ContextMenu();

        MenuItem add = new MenuItem("新增分類");
        add.setOnAction(e -> controller.showAddCategoryInput());

        MenuItem del = new MenuItem("刪除此分類");
        del.setOnAction(e -> {
            controller.getTaskCategoryChoice().getItems().remove(category);
            refreshCategoryTabs();
            controller.refreshTaskViews();
        });

        menu.getItems().addAll(add, del);
        button.setContextMenu(menu);

        return button;
    }

    private void highlightTab(String selected) {
        for (var node : controller.getCategoryTabs().getChildren()) {
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
