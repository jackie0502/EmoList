package com.app.emolist.GUI.TaskPanel;

import com.app.emolist.Controller.Task;
import com.app.emolist.GUI.TaskPanelController;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.util.List;
import java.util.stream.Collectors;

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
                    return;
                }

                // 取得原始任務物件（用 ID 比對）
                Task actualTask = controller.getTaskManager().getAllTasks().stream()
                        .filter(t -> t.getId().equals(task.getId()))
                        .findFirst()
                        .orElse(task); // 如果找不到就用目前這個（但通常會找到）

                String text = actualTask.getTitle();
                if (actualTask.getDeadline() != null) {
                    text += " (" + actualTask.getDeadline() + ")";
                }

                CheckBox checkBox = new CheckBox(text);
                checkBox.setSelected(controller.getSelectedTasks().contains(actualTask));

                checkBox.setOnAction(e -> {
                    if (checkBox.isSelected()) {
                        controller.getSelectedTasks().add(actualTask);
                    } else {
                        controller.getSelectedTasks().remove(actualTask);
                    }
                });

                setGraphic(checkBox);
            }
        };
    }


    public void refreshTaskViews() {
        String query = controller.getSearchField().getText().trim().toLowerCase();
        String filter = controller.getCurrentCategoryFilter();

        List<Task> uncompleted = controller.getTaskManager().getAllTasks().stream()
                .filter(t -> !t.isCompleted()
                        && (filter.equals("全部") || t.getCategory().equals(filter))
                        && (query.isEmpty()
                        || t.getTitle().toLowerCase().contains(query)
                        || t.getCategory().toLowerCase().contains(query)))
                .collect(Collectors.toList());

        List<Task> completed = controller.getTaskManager().getAllTasks().stream()
                .filter(t -> t.isCompleted()
                        && (filter.equals("全部") || t.getCategory().equals(filter))
                        && (query.isEmpty()
                        || t.getTitle().toLowerCase().contains(query)
                        || t.getCategory().toLowerCase().contains(query)))
                .collect(Collectors.toList());

        controller.getUncompletedListView().getItems().setAll(uncompleted);
        controller.getCompletedListView().getItems().setAll(completed);
        controller.getUncompletedListView().refresh();
        controller.getCompletedListView().refresh();
//        System.out.println("== ListView 比對 ==");
//        System.out.println("UncompletedView: " + controller.getUncompletedListView());
//        System.out.println("CompletedView: " + controller.getCompletedListView());
//        System.out.println("是否為同一個物件？ " + (controller.getUncompletedListView() == controller.getCompletedListView()));

    }
}
