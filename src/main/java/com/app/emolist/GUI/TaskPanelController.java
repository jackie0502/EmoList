package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskPanelController {
    @FXML private ListView<Task> taskListView;
    @FXML private TextField searchField;
    @FXML private TextField inputField;
    @FXML private ComboBox<String> categoryChoice;
    @FXML private ComboBox<String> priorityChoice;
    @FXML private ComboBox<String> recurrenceChoice;
    @FXML private TextField tagField;
    @FXML private CheckBox darkModeToggle;

    private TaskManager taskManager;
    private final TaskRepository taskRepo = new TaskRepository();
    private boolean darkMode = false;

    // 注入用
    private CalendarPanelController calendarController;
    private StatsPanelController statsController;

    @FXML
    private void initialize() {
        configureChoices();
        configureListView();
        searchField.setOnKeyReleased(e -> filterTasks());
    }

    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        taskListView.getItems().setAll(taskManager.getTasks());
    }

    public void setCalendarController(CalendarPanelController controller) {
        this.calendarController = controller;
    }

    public void setStatsController(StatsPanelController controller) {
        this.statsController = controller;
    }

    private void configureChoices() {
        categoryChoice.getItems().addAll("工作", "個人", "其他");
        priorityChoice.getItems().addAll("低", "中", "高");
        recurrenceChoice.getItems().addAll("無", "每日", "每週", "每月");
    }

    private void configureListView() {
        taskListView.setCellFactory(listView -> {
            ListCell<Task> cell = new ListCell<>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    if (empty || task == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(task.toString());
                        setTextFill(darkMode ? Color.WHITE : Color.BLACK);
                        setStyle(darkMode ?
                                "-fx-background-color: #444444; -fx-border-color: #777777; -fx-border-radius: 4;" :
                                "-fx-border-color: #cccccc; -fx-border-radius: 4;");
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.getItem() == null) return;
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(Integer.toString(taskListView.getItems().indexOf(cell.getItem())));
                db.setContent(content);
                event.consume();
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                if (cell.getItem() == null) return;
                int draggedIndex = Integer.parseInt(event.getDragboard().getString());
                Task draggedTask = taskListView.getItems().remove(draggedIndex);
                int thisIndex = taskListView.getItems().indexOf(cell.getItem());
                taskListView.getItems().add(thisIndex, draggedTask);
                refreshTaskManager();
                event.setDropCompleted(true);
                event.consume();
            });

            return cell;
        });
    }

    @FXML
    private void handleAddTask() {
        String title = inputField.getText().trim();
        if (!title.isEmpty()) {
            Task task = new Task(title, LocalDate.now());
            task.setCategory(categoryChoice.getValue() != null ? categoryChoice.getValue() : "其他");
            String priorityText = priorityChoice.getValue() != null ? priorityChoice.getValue() : "中";
            int priority = switch (priorityText) {
                case "高" -> 3;
                case "中" -> 2;
                default -> 1;
            };
            task.setPriority(priority);
            task.setRecurrence(recurrenceChoice.getValue() != null ? recurrenceChoice.getValue() : "無");
            task.setTags(tagField.getText());
            taskManager.addTask(task);
            taskListView.getItems().add(task);
            inputField.clear();
            tagField.clear();
            updatePanels();
        }
    }

    @FXML
    private void handleCompleteTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.toggleCompleted();
            if (selected.isCompleted() && selected.isRecurring()) {
                LocalDate nextDate = switch (selected.getRecurrence()) {
                    case "每日" -> selected.getDeadline().plusDays(1);
                    case "每週" -> selected.getDeadline().plusWeeks(1);
                    case "每月" -> selected.getDeadline().plusMonths(1);
                    default -> selected.getDeadline();
                };
                Task newTask = new Task(selected.getTitle(), nextDate);
                newTask.setCategory(selected.getCategory());
                newTask.setPriority(selected.getPriority());
                newTask.setTags(selected.getTags());
                newTask.setRecurrence(selected.getRecurrence());
                taskManager.addTask(newTask);
                taskListView.getItems().add(newTask);
                showAlert("已新增下一次的週期性任務: " + selected.getTitle() + "，截止日期: " + nextDate, Alert.AlertType.INFORMATION);
            }
            taskListView.refresh();
            updatePanels();
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskManager.getTasks().remove(selected);
            taskListView.getItems().remove(selected);
            updatePanels();
        }
    }

    @FXML
    private void handleExportTasks() {
        taskRepo.saveTasks(taskManager.getTasks());
        showAlert("任務已匯出到 tasks.json", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void toggleDarkMode() {
        darkMode = darkModeToggle.isSelected();
        if (taskListView.getScene() != null) {
            if (darkMode) {
                taskListView.getScene().getRoot().getStyleClass().add("dark-mode");
            } else {
                taskListView.getScene().getRoot().getStyleClass().remove("dark-mode");
            }
        }
        taskListView.refresh();
    }


    private void filterTasks() {
        String query = searchField.getText().trim().toLowerCase();
        taskListView.getItems().clear();
        for (Task task : taskManager.getTasks()) {
            if (task.getTitle().toLowerCase().contains(query) ||
                    task.getCategory().toLowerCase().contains(query) ||
                    task.getTags().toLowerCase().contains(query)) {
                taskListView.getItems().add(task);
            }
        }
    }

    private void updatePanels() {
        if (statsController != null) statsController.updateCharts();
        if (calendarController != null) calendarController.refreshCalendarView();
    }

    private void refreshTaskManager() {
        taskManager.getTasks().clear();
        taskManager.getTasks().addAll(taskListView.getItems());
        updatePanels();
    }

    public void checkDeadlines() {
        LocalDate today = LocalDate.now();
        StringBuilder overdueList = new StringBuilder();
        for (Task task : taskManager.getTasks()) {
            if (!task.isCompleted() && (task.getDeadline().isBefore(today) || task.getDeadline().equals(today))) {
                String status = task.getDeadline().isBefore(today) ? "（已過期）" : "（今天截止）";
                overdueList.append(task.toString()).append(status).append("\n");
            }
        }
        if (overdueList.length() > 0) {
            showAlert("以下任務已到期或即將到期：\n" + overdueList, Alert.AlertType.INFORMATION);
        }
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.WARNING ? "警告" : "通知");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
