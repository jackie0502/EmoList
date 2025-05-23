package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class TaskViewHelper {

    private final TaskPanelController controller;

    public TaskViewHelper(TaskPanelController controller) {
        this.controller = controller;
    }

    public void setupListViews() {
        controller.getUncompletedListView().setCellFactory(list -> createTaskCell(false));
        controller.getCompletedListView().setCellFactory(list -> createTaskCell(true));

        controller.getSearchField().setOnKeyReleased(e -> controller.refreshTaskViews());
        controller.refreshTaskViews();
    }

    private ListCell<Task> createTaskCell(boolean completed) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                } else {
                    String text = task.getTitle();
                    if (task.getDeadline() != null) {
                        text += " (" + task.getDeadline() + ")";
                    }

                    CheckBox checkBox = new CheckBox(text);
                    checkBox.setSelected(task.isCompleted());
                    checkBox.setOnAction(e -> {
                        task.setCompleted(checkBox.isSelected());
                        controller.refreshTaskViews();
                        controller.updatePanels();
                    });

                    setGraphic(checkBox);
                }
            }
        };
    }

    public void refreshTaskViews() {
        String query = controller.getSearchField().getText().trim().toLowerCase();
        String filter = controller.getCurrentCategoryFilter();

        controller.getUncompletedListView().getItems().setAll(
                controller.getTaskManager().getAllTasks().stream()
                        .filter(t -> !t.isCompleted()
                                && (filter.equals("全部") || t.getCategory().equals(filter))
                                && (query.isEmpty() || t.getTitle().toLowerCase().contains(query)
                                || t.getCategory().toLowerCase().contains(query)))
                        .toList()
        );

        controller.getCompletedListView().getItems().setAll(
                controller.getTaskManager().getAllTasks().stream()
                        .filter(t -> t.isCompleted()
                                && (filter.equals("全部") || t.getCategory().equals(filter))
                                && (query.isEmpty() || t.getTitle().toLowerCase().contains(query)
                                || t.getCategory().toLowerCase().contains(query)))
                        .toList()
        );
    }
}
