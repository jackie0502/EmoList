package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import com.app.emolist.GUI.TaskPanel.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

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
    @FXML private VBox taskInputBox;

    private final TaskManager taskManager = new TaskManager();
    private final TaskRepository taskRepo = new TaskRepository();

    private String currentCategoryFilter = "全部";

    // 子模組
    private final CategoryHelper categoryHelper = new CategoryHelper(this);
    private final TaskInputHelper taskInputHelper = new TaskInputHelper(this);
    private final TaskViewHelper taskViewHelper = new TaskViewHelper(this);
    private final DeadlineHelper deadlineHelper = new DeadlineHelper(this);
    private CalendarPanelController calendarController;
    private StatsPanelController statsController;


    @FXML
    private void initialize() {
        taskCategoryChoice.getItems().addAll("工作", "娛樂", "無");
        taskCategoryChoice.getSelectionModel().select("無");
        priorityChoice.getItems().addAll("低", "中", "高");
        priorityChoice.getSelectionModel().select("中");

        recurrenceChoice.getItems().addAll("無", "每日", "每週", "每月");
        recurrenceChoice.getSelectionModel().select("無");


        categoryHelper.refreshCategoryTabs();
        taskInputHelper.hideTaskInputBox();
        taskViewHelper.setupListViews();
    }

    // 👇 提供給子模組呼叫的橋接方法們

    public void showAddCategoryInput() {
        addCategoryBox.setVisible(true);
        addCategoryBox.setManaged(true);
        addCategoryField.clear();
        categoryMessage.setText("");
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager.getAllTasks().clear();
        this.taskManager.getAllTasks().addAll(taskManager.getAllTasks());
    }

    public void setCalendarController(CalendarPanelController calendarController) {
        this.calendarController = calendarController;
    }

    public void setStatsController(StatsPanelController statsController) {
        this.statsController = statsController;
    }

    @FXML
    private void handleShowAddCategory() {
        showAddCategoryInput(); // 或直接在這裡顯示分類輸入欄位
    }



    @FXML
    private void handleConfirmAddCategory() {
        String newCategory = addCategoryField.getText().trim();
        if (newCategory.isEmpty()) {
            categoryMessage.setText("分類不能為空");
        } else if (getVisualLength(newCategory) > 10) {
            categoryMessage.setText("分類長度不能超過 10（中文算2）");
        } else if (taskCategoryChoice.getItems().contains(newCategory)) {
            categoryMessage.setText("分類已存在");
        } else {
            taskCategoryChoice.getItems().add(newCategory);
            categoryHelper.refreshCategoryTabs();
            categoryMessage.setText("已新增分類：" + newCategory);
            addCategoryBox.setVisible(false);
            addCategoryBox.setManaged(false);
        }
    }

    @FXML
    private void handleCancelAddCategory() {
        addCategoryBox.setVisible(false);
        addCategoryBox.setManaged(false);
        categoryMessage.setText("");
    }

    @FXML
    private void handleShowTaskInput() {
        taskInputHelper.showTaskInputBox();
    }

    @FXML
    private void handleAddTask() {
        taskInputHelper.handleAddTask();
    }

    @FXML
    private void handleCancelAddTask() {
        taskInputHelper.hideTaskInputBox();
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = uncompletedListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = completedListView.getSelectionModel().getSelectedItem();
        }
        if (selected != null) {
            taskManager.getAllTasks().remove(selected);
            refreshTaskViews();
            updatePanels();
        }
    }

    @FXML
    private void handleExportTasks() {
        taskRepo.saveTasks(taskManager.getAllTasks());
    }

    @FXML
    private void toggleDarkMode() {
        boolean dark = darkModeToggle.isSelected();
        if (uncompletedListView.getScene() != null) {
            if (dark) {
                uncompletedListView.getScene().getRoot().getStyleClass().add("dark-mode");
            } else {
                uncompletedListView.getScene().getRoot().getStyleClass().remove("dark-mode");
            }
        }
    }

    public void refreshTaskViews() {
        taskViewHelper.refreshTaskViews();
    }

//    public LocalDate getDeadLine(){
//        deadlineHelper
//    }
    public void checkDeadlines() {
        deadlineHelper.checkDeadlines();
    }

    public void updatePanels() {
        // 若有 stats/calendar 可由這裡串接更新
    }

    // 🔧 視覺長度工具
    private int getVisualLength(String s) {
        int length = 0;
        for (char c : s.toCharArray()) {
            if (Character.UnicodeBlock.of(c).toString().contains("CJK") ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }

    @FXML private DatePicker deadlinePicker;

    public DatePicker getTaskDeadlinePicker() {
        return deadlinePicker;
    }

    @FXML
    private void handleClearDeadline() {
        deadlinePicker.setValue(null);
    }

    public CalendarPanelController getCalendarController() {
        return calendarController;
    }

    // 👉 getter
    public TaskManager getTaskManager() { return taskManager; }
    public TaskRepository getTaskRepo() { return taskRepo; }

    public HBox getCategoryTabs() { return categoryTabs; }
    public ComboBox<String> getCategoryDropdown() { return categoryDropdown; }
    public HBox getAddCategoryBox() { return addCategoryBox; }
    public TextField getAddCategoryField() { return addCategoryField; }
    public Label getCategoryMessage() { return categoryMessage; }

    public TextField getSearchField() { return searchField; }
    public TextField getInputField() { return inputField; }
    public ComboBox<String> getTaskCategoryChoice() { return taskCategoryChoice; }
    public ComboBox<String> getPriorityChoice() { return priorityChoice; }
    public ComboBox<String> getRecurrenceChoice() { return recurrenceChoice; }

    public ListView<Task> getUncompletedListView() { return uncompletedListView; }
    public ListView<Task> getCompletedListView() { return completedListView; }

    public VBox getTaskInputBox() { return taskInputBox; }

    public String getCurrentCategoryFilter() { return currentCategoryFilter; }
    public void setCurrentCategoryFilter(String filter) { this.currentCategoryFilter = filter; }
}
