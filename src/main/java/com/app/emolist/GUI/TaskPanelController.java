package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.util.List;

public class TaskPanelController {
    @FXML private HBox categoryTabs;
    @FXML private ComboBox<String> categoryDropdown;
    @FXML private ComboBox<String> taskCategoryChoice;
    @FXML private ListView<Task> uncompletedListView;
    @FXML private ListView<Task> completedListView;
    @FXML private TextField searchField;
    @FXML private TextField inputField;
    @FXML private ComboBox<String> priorityChoice;
    @FXML private ComboBox<String> recurrenceChoice;
    @FXML private CheckBox darkModeToggle;

    private TaskManager taskManager;
    private final TaskRepository taskRepo = new TaskRepository();
    private boolean darkMode = false;

    private CalendarPanelController calendarController;
    private StatsPanelController statsController;

    private final int MAX_VISIBLE_TABS = 4;
    private final List<String> allCategories = List.of("工作", "個人", "學習", "娛樂", "家庭", "運動", "其他", "專案");
    private String currentCategoryFilter = "全部";

    @FXML
    private void initialize() {
        configureChoices();
        configureListViews();
        setupCategoryTabs();
        searchField.setOnKeyReleased(e -> refreshTaskViews());
    }

    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        refreshTaskViews();
    }

    public void setCalendarController(CalendarPanelController controller) {
        this.calendarController = controller;
    }

    public void setStatsController(StatsPanelController controller) {
        this.statsController = controller;
    }

    private void configureChoices() {
        taskCategoryChoice.getItems().addAll(allCategories);
        taskCategoryChoice.getSelectionModel().select("其他");

        priorityChoice.getItems().addAll("低", "中", "高");
        recurrenceChoice.getItems().addAll("無", "每日", "每週", "每月");
    }

    private void configureListViews() {
        uncompletedListView.setCellFactory(list -> createTaskCell());
        completedListView.setCellFactory(list -> createTaskCell());
    }

    private ListCell<Task> createTaskCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox(task.getTitle() + (task.getDeadline() != null ? " (" + task.getDeadline() + ")" : ""));
                    checkBox.setSelected(task.isCompleted());

                    checkBox.setOnAction(e -> {
                        task.setCompleted(checkBox.isSelected());
                        refreshTaskViews();
                        updatePanels();
                    });

                    setGraphic(checkBox);
                }
            }
        };
    }
    private void setupCategoryTabs() {
        categoryTabs.getChildren().clear();
        categoryDropdown.getItems().clear();

        List<String> visibleCategories = allCategories.size() > MAX_VISIBLE_TABS ? allCategories.subList(0, MAX_VISIBLE_TABS) : allCategories;
        List<String> overflowCategories = allCategories.size() > MAX_VISIBLE_TABS ? allCategories.subList(MAX_VISIBLE_TABS, allCategories.size()) : List.of();

        Button allButton = new Button("全部");
        allButton.setOnAction(e -> {
            currentCategoryFilter = "全部";
            refreshTaskViews();
            highlightSelectedTab("全部");
        });
        categoryTabs.getChildren().add(allButton);

        for (String category : visibleCategories) {
            Button tabButton = new Button(category);
            tabButton.setOnAction(e -> {
                currentCategoryFilter = category;
                refreshTaskViews();
                highlightSelectedTab(category);
            });
            categoryTabs.getChildren().add(tabButton);
        }

        if (!overflowCategories.isEmpty()) {
            categoryDropdown.setVisible(true);
            categoryDropdown.getItems().setAll(overflowCategories);
            categoryDropdown.setOnAction(e -> {
                currentCategoryFilter = categoryDropdown.getValue();
                refreshTaskViews();
                highlightSelectedTab(null);
            });
        } else {
            categoryDropdown.setVisible(false);
        }

        highlightSelectedTab("全部");
    }

    private void highlightSelectedTab(String selected) {
        categoryTabs.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                if (button.getText().equals(selected)) {
                    button.setStyle("-fx-background-color: #90caf9;");
                } else {
                    button.setStyle("");
                }
            }
        });
    }

    private void refreshTaskViews() {
        String query = searchField.getText().trim().toLowerCase();

        uncompletedListView.getItems().setAll(taskManager.getTasks().stream()
                .filter(t -> !t.isCompleted()
                        && (currentCategoryFilter.equals("全部") || t.getCategory().equals(currentCategoryFilter))
                        && (query.isEmpty() ||
                        t.getTitle().toLowerCase().contains(query) ||
                        t.getCategory().toLowerCase().contains(query)))
                .toList());


        completedListView.getItems().setAll(taskManager.getTasks().stream()
                .filter(t -> t.isCompleted()
                        && (currentCategoryFilter.equals("全部") || t.getCategory().equals(currentCategoryFilter))
                        && (query.isEmpty() ||
                        t.getTitle().toLowerCase().contains(query) ||
                        t.getCategory().toLowerCase().contains(query)))
                .toList());
    }

    @FXML
    private void handleAddTask() {
        String title = inputField.getText().trim();
        if (!title.isEmpty()) {
            Task task = new Task(title, LocalDate.now());
            String category = taskCategoryChoice.getValue() != null ? taskCategoryChoice.getValue() : "其他";
            task.setCategory(category);
            task.setTags(category); // 標籤等於分類
            String priorityText = priorityChoice.getValue() != null ? priorityChoice.getValue() : "中";
            int priority = switch (priorityText) {
                case "高" -> 3;
                case "中" -> 2;
                default -> 1;
            };
            task.setPriority(priority);
            task.setRecurrence(recurrenceChoice.getValue() != null ? recurrenceChoice.getValue() : "無");
            taskManager.addTask(task);
            inputField.clear();
            refreshTaskViews();
            updatePanels();
        }
    }


    @FXML
    private void handleDeleteTask() {
        Task selected = uncompletedListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = completedListView.getSelectionModel().getSelectedItem();
        }
        if (selected != null) {
            taskManager.getTasks().remove(selected);
            refreshTaskViews();
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
        if (uncompletedListView.getScene() != null) {
            if (darkMode) {
                uncompletedListView.getScene().getRoot().getStyleClass().add("dark-mode");
            } else {
                uncompletedListView.getScene().getRoot().getStyleClass().remove("dark-mode");
            }
        }
        uncompletedListView.refresh();
        completedListView.refresh();
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

    private void updatePanels() {
        if (statsController != null) statsController.updateCharts();
        if (calendarController != null) calendarController.refreshCalendarView();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.WARNING ? "警告" : "通知");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

