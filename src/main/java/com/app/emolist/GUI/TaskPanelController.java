package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import java.time.LocalDate;
import java.util.List;

public class TaskPanelController {
    @FXML private HBox categoryTabs;
    @FXML private ComboBox<String> categoryDropdown;
    @FXML private HBox addCategoryBox;
    @FXML private TextField addCategoryField;
    @FXML private Label categoryMessage;
    @FXML private ComboBox<String> taskCategoryChoice;
    @FXML private ListView<Task> uncompletedListView;
    @FXML private ListView<Task> completedListView;
    @FXML private TextField searchField;
    @FXML private TextField inputField;
    @FXML private ComboBox<String> priorityChoice;
    @FXML private ComboBox<String> recurrenceChoice;
    @FXML private CheckBox darkModeToggle;
    @FXML private Region categorySpacer;

    private TaskManager taskManager;
    private final TaskRepository taskRepo = new TaskRepository();
    private boolean darkMode = false;

    private CalendarPanelController calendarController;
    private StatsPanelController statsController;

    private final int MAX_VISIBLE_TABS = 5;
    private final ObservableList<String> allCategories = FXCollections.observableArrayList("無", "娛樂", "工作");
    private String currentCategoryFilter = "全部";



    @FXML
    private void initialize() {
        configureChoices();
        configureListViews();
        setupCategoryTabs();
        searchField.setOnKeyReleased(e -> refreshTaskViews());
        categoryMessage.setText("");
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
        taskCategoryChoice.setItems(allCategories);
        taskCategoryChoice.getSelectionModel().select("無");

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

    @FXML
    private void handleShowAddCategory() {
        addCategoryBox.setVisible(true);
        addCategoryField.clear();
        categoryMessage.setText("");
    }

    @FXML
    private void handleConfirmAddCategory() {
        String newCategory = addCategoryField.getText().trim();
        if (newCategory.isEmpty()) {
            categoryMessage.setText("分類不能為空");
        } else if (getVisualLength(newCategory) > 12) {
            categoryMessage.setText("中/英 文需小於 6/12 字");
        } else if (allCategories.contains(newCategory)) {
            categoryMessage.setText("分類已存在");
        } else {
            allCategories.add(newCategory);
            setupCategoryTabs();
            categoryMessage.setText("已新增分類：" + newCategory);
            addCategoryBox.setVisible(false);
        }


    }

    @FXML
    private void handleCancelAddCategory() {
        addCategoryBox.setVisible(false);
        categoryMessage.setText("");
    }
    private void setupCategoryTabs() {
        categoryTabs.getChildren().clear();
        categoryDropdown.getItems().clear();

        Button allButton = new Button("全部");
        allButton.setOnAction(e -> {
            currentCategoryFilter = "全部";
            refreshTaskViews();
            highlightSelectedTab("全部");
        });
        categoryTabs.getChildren().add(allButton);

        List<String> visibleCategories = allCategories.size() > MAX_VISIBLE_TABS ? allCategories.subList(0, MAX_VISIBLE_TABS) : allCategories;
        List<String> overflowCategories = allCategories.size() > MAX_VISIBLE_TABS ? allCategories.subList(MAX_VISIBLE_TABS, allCategories.size()) : List.of();

        for (String category : visibleCategories) {
            if (category.equals("無")) continue;
            Button tabButton = createCategoryTabButton(category);
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

    private Button createCategoryTabButton(String category) {
        Button tabButton = new Button(category);
        tabButton.setOnAction(e -> {
            currentCategoryFilter = category;
            refreshTaskViews();
            highlightSelectedTab(category);
        });

        // 右鍵選單
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addCategory = new MenuItem("新增分類");
        addCategory.setOnAction(e -> handleShowAddCategory());

        MenuItem deleteCategory = new MenuItem("刪除此分類");
        deleteCategory.setOnAction(e -> {
            allCategories.remove(category);
            setupCategoryTabs();
            refreshTaskViews();
        });

        contextMenu.getItems().addAll(addCategory, deleteCategory);
        tabButton.setContextMenu(contextMenu);

        return tabButton;
    }

    private int getVisualLength(String s) {
        int length = 0;
        for (char c : s.toCharArray()) {
            // 中文、日文、韓文、全形符號
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
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
                        && (query.isEmpty() || t.getTitle().toLowerCase().contains(query) || t.getCategory().toLowerCase().contains(query)))
                .toList());

        completedListView.getItems().setAll(taskManager.getTasks().stream()
                .filter(t -> t.isCompleted()
                        && (currentCategoryFilter.equals("全部") || t.getCategory().equals(currentCategoryFilter))
                        && (query.isEmpty() || t.getTitle().toLowerCase().contains(query) || t.getCategory().toLowerCase().contains(query)))
                .toList());
    }

    @FXML
    private void handleAddTask() {
        String title = inputField.getText().trim();
        if (!title.isEmpty()) {
            Task task = new Task(title, LocalDate.now());
            String category = taskCategoryChoice.getValue() != null ? taskCategoryChoice.getValue() : "其他";
            task.setCategory(category);
            task.setTags(category);
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

    @FXML
    private void handleExportTasks() {
        taskRepo.saveTasks(taskManager.getTasks());
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

    private void updatePanels() {
        if (statsController != null) statsController.updateCharts();
        if (calendarController != null) calendarController.refreshCalendarView();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        // 留空或可以改用內嵌 Label
    }
}
